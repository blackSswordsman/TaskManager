package com.example.taskmanager.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Setter
@Accessors(chain = true)
@Schema(description = "Страница с данными")
public class PageResponse<T> {

    @Schema(description = "Содержимое страницы", example = "[{...}, {...}]")
    private List<T> content;

    @Schema(description = "Номер текущей страницы", example = "0")
    private int pageNumber;

    @Schema(description = "Размер страницы", example = "10")
    private int pageSize;

    @Schema(description = "Общее количество элементов", example = "100")
    private long totalElements;

    @Schema(description = "Общее количество страниц", example = "10")
    private int totalPages;

    public static <T> PageResponse<T> valueOf(Page<T> page) {
        return new PageResponse<T>()
                .setContent(page.getContent())
                .setPageNumber(page.getNumber())
                .setPageSize(page.getSize())
                .setTotalPages(page.getTotalPages())
                .setTotalElements(page.getTotalElements());
    }
}
