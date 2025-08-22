package umc.plantory.global.enums;

import java.util.List;

public enum DiaryStatus {
    NORMAL, SCRAP, TEMP, DELETE;

    public static final List<DiaryStatus> VALID_STATUSES = List.of(NORMAL, SCRAP);}
