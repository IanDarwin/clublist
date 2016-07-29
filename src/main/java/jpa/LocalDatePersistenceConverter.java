package jpa;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/** JPA Converter for LocalDateTime, remove when JPA catches up with Java 8
 * @author Steven Gertiser, https://weblogs.java.net/blog/montanajava/archive/2014/06/17/using-java-8-datetime-classes-jpa
 */
@Converter(autoApply = true)
public class LocalDatePersistenceConverter
	implements AttributeConverter<LocalDate, Date> {
	
    @Override
    public java.sql.Date convertToDatabaseColumn(LocalDate entityValue) {
        return Date.valueOf(entityValue);
    }

	@Override
	public LocalDate convertToEntityAttribute(Date databaseValue) {
		return databaseValue.toLocalDate();
	}
}
