package org.lvelez.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Book {
    @NotBlank
    private String isbn;
    @NotBlank
    private String name;
    @NotNull
    private BookGenre genre;
    @NotBlank
    private String author;
    private String description;
    private String language;
    private int numPages;
}