package org.lvelez.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Book {
    @NotEmpty
    private String isbn;
    @NotEmpty
    private String name;
    @NotNull
    private BookGenre genre;
    @NotEmpty
    private String author;
    private String description;
    private String language;
    private int numPages;
}