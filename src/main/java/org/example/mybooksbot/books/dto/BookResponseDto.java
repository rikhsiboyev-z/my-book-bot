package org.example.mybooksbot.books.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookResponseDto {
    private String name;
    private String author;
    private Integer size;
    private LocalDate date;
    private String price;

    @Override
    public String toString() {
        return String.format("✨ Kitob ma'lumotlari ✨\n" +
                "📚 Ismi: %s\n" +
                "✍️ Muallif: %s\n" +
                "📏 Hajmi: %s\n" +
                "💵 Narxi: %s\n", name, author, size, price);
    }

}
