package umc.plantory.domain.event.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class S3DeleteEvent {
    private final List<String> urlList;
}
