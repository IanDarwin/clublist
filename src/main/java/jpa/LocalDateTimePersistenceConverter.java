package jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/** JPA Converter for LocalDateTime, remove when JPA catches up with Java 8
 * @author Steven Gertiser, https://weblogs.java.net/blog/montanajava/archive/2014/06/17/using-java-8-datetime-classes-jpa
 */
@Converter(autoApply = true)
public class LocalDateTimePersistenceConverter
	implements AttributeConverter<LocalDateTime, Timestamp> {
	
    @Override
    public java.sql.Timestamp convertToDatabaseColumn(LocalDateTime entityValue) {
        return Timestamp.valueOf(entityValue);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(java.sql.Timestamp databaseValue) {
        return databaseValue.toLocalDateTime();
    }
}
