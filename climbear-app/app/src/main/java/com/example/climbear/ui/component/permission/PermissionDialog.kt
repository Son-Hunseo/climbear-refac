package com.example.climbear.ui.component.permission

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.climbear.R

data class PermissionDialogData(
    val title: String,
    val permissionType: String,
    val descriptionPrefix: String,
    val descriptionHighlight: String,
    val descriptionSuffix: String,
    val manualGuideTitle: String,
    val manualGuide: String,
    val imageResourceId: Int = R.drawable.logo
)

@Composable
fun PermissionDialog(
    data: PermissionDialogData,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(data.imageResourceId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                )
                Text(
                    text = data.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = data.permissionType,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0x14, 0x14, 0x78)
                )
                Text(
                    buildAnnotatedString {
                        append(data.descriptionPrefix)
                        withStyle(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(data.descriptionHighlight)
                        }
                        append(data.descriptionSuffix)
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xF2, 0XF2, 0xFB),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = data.manualGuideTitle,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0x14, 0x14, 0x78),
                        fontSize = 12.sp
                    )
                    Text(
                        text = data.manualGuide,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(
                    text = stringResource(R.string.confirm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = onConfirm
                        ),
                    textAlign = TextAlign.Center
                )

            }
        }
    }
}

@Preview
@Composable
fun PreviewScreen() {
    PermissionDialog(
        PermissionDialogData(
            title = stringResource(R.string.camera_permission_title),
            permissionType = stringResource(R.string.camera_permission_type),
            descriptionPrefix = stringResource(R.string.camera_permission_description_prefix),
            descriptionHighlight = stringResource(R.string.camera_permission_description_highlight),
            descriptionSuffix = stringResource(R.string.camera_permission_description_suffix),
            manualGuideTitle = stringResource(R.string.camera_permission_manual_title),
            manualGuide = stringResource(R.string.camera_permission_manual)
        ),
        {}, {})
}