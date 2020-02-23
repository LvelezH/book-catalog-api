package org.lvelez.book;

import com.mongodb.MongoClientSettings;
import org.apache.commons.lang3.StringUtils;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

public class BookCodec implements CollectibleCodec<Book> {
    private final Codec<Document> documentCodec;

    public BookCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public Book generateIdIfAbsentFromDocument(Book book) {
        if (!documentHasId(book)) {
            book.setIsbn(UUID.randomUUID().toString());
        }
        return book;
    }

    @Override
    public boolean documentHasId(Book book) {
        return book.getIsbn() != null;
    }

    @Override
    public BsonValue getDocumentId(Book book) {
        return new BsonString(book.getIsbn());
    }

    @Override
    public Book decode(BsonReader reader, DecoderContext decoderContext) {
        Document doc = documentCodec.decode(reader, decoderContext);
        Book book = new Book();
        if (doc.getString("_id") != null) {
            book.setIsbn(doc.getString("_id"));
        }

        book.setName(doc.getString("name"));

        if (StringUtils.isNotBlank(doc.getString("genre"))) {
            book.setGenre(BookGenre.valueOf(doc.getString("genre")));
        }

        book.setAuthor(doc.getString("author"));
        book.setDescription(doc.getString("description"));
        book.setLanguage(doc.getString("language"));
        book.setNumPages(doc.getInteger("numPages"));

        return book;
    }

    @Override
    public void encode(BsonWriter writer, Book book, EncoderContext encoderContext) {
        Document doc = new Document()
                .append("_id", book.getIsbn())
                .append("name", book.getName())
                .append("genre", book.getGenre().toString())
                .append("author", book.getAuthor())
                .append("description", book.getDescription())
                .append("language", book.getLanguage())
                .append("numPages", book.getNumPages());

        documentCodec.encode(writer, doc, encoderContext);
    }

    @Override
    public Class<Book> getEncoderClass() {
        return Book.class;
    }
}
