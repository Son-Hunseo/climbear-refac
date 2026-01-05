## To-do

```bash
mkdir sam

cd sam

curl https://dl.fbaipublicfiles.com/segment_anything/sam_vit_b_01ec64.pth
```

- SAM 모델의 가중치가 파일 크기가 커서 git으로 관리하기에 비효율적이다. (변할 일도 없으므로)
- 이에 프로젝트를 `git clone` 하고 위 명령어로 SAM 가중치 파일을 넣어주는 것이 효율적임