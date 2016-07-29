package webtier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass=LocalDate.class)
public class LocalDateJsfConverter implements Converter {

	final String pattern = "MMMM d, yyyy";
	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String date;
		try {
			date = formatter.format((LocalDate) value);
		} catch (Exception e){
			throw  new ConverterException(e);
		}
		return date;
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		LocalDate localDate = null;
		try {
			final TemporalAccessor parsed = formatter.parse(value);
			localDate = LocalDate.from(parsed);
		} catch (Exception e){
			throw new ConverterException(e.toString(), e);
		}
		return localDate;
	}
}
