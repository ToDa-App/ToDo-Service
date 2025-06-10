package com.toda.ToDo_Service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(name = "PagedResponse", description = "Generic response wrapper for paginated results")
public class PagedResponse<T>{
    @Schema(description = "List of items for the current page")
    private List<T> content;
    @Schema(description = "Current page number (0-based)", example = "0")
    private int pageNumber;
    @Schema(description = "Size of the page (number of items)", example = "10")
    private int pageSize;
    @Schema(description = "Total number of elements across all pages", example = "125")
    private long totalElements;
    @Schema(description = "Total number of pages available", example = "13")
    private int totalPages;

    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
