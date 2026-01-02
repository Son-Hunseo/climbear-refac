package com.example.climbear.background

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.climbear.data.record.model.Route
import com.example.climbear.util.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


class VideoAnalysisWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_VIDEO_URI       = "video_uri"
        const val KEY_HOLD_FILE       = "hold_file"
        const val KEY_SELECTED_IDS    = "selected_ids"
        private const val TAG = "VideoAnalysisWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val fileName = inputData.getString(KEY_HOLD_FILE)
            ?: return@withContext Result.failure()
        val holdFile = File(appContext.filesDir, fileName)
        if (!holdFile.exists()) return@withContext Result.failure()

        val uriString = inputData.getString(KEY_VIDEO_URI)
            ?: return@withContext Result.failure()
        val videoUri = Uri.parse(uriString)

        val selectedHoldIds: Set<Int> =
            inputData.getIntArray(KEY_SELECTED_IDS)
                ?.toSet()
                ?: emptySet()

        data class HoldEntry(
            @SerializedName("hold_id")
            val holdId: Int,
            @SerializedName("coordinates")
            val points: List<Point>,
        )

        val rawJson = holdFile.readText()
        val entriesType = object : TypeToken<List<HoldEntry>>() {}.type
        val entries: List<HoldEntry> = GsonBuilder().create().fromJson(rawJson, entriesType)
        val holdCoordinates: Map<Int, List<Point>> = entries.associate { it.holdId to it.points }
        val filteredHoldCoordinates = holdCoordinates.filterKeys { it in selectedHoldIds }

        try {
            val poseAnalyzer = PoseAnalyzer(appContext)

            Log.d(TAG, "Starting video analysis...")
            val logs: List<FrameLog> = poseAnalyzer.analyzeVideoUri(
                videoUri = videoUri,
                holdCoordinates = filteredHoldCoordinates
            )
            Log.d(TAG, "Video analysis complete. ${logs.size} logs generated.")

            if (logs.isEmpty()) {
                Log.w(TAG, "Analysis resulted in empty logs.")
            }

            val logFile = File(appContext.filesDir, "hold_log.json")
            logFile.bufferedWriter().use {
                it.write(GsonBuilder().serializeNulls().create().toJson(logs))
            }
            Log.d(TAG, "Log file saved to ${logFile.absolutePath}")

            val route = Route(
                leftHand  = logs.map { it.left_hand  },
                rightHand = logs.map { it.right_hand },
                leftFoot  = logs.map { it.left_foot  },
                rightFoot = logs.map { it.right_foot }
            )
            Log.d(TAG, "Route created successfully: $route")

            return@withContext Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Video analysis failed", e)
            return@withContext Result.failure()
        }
    }
}
