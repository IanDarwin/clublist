package mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

@Dependent
public class MockDataSourceProducer {

		@Produces
		public DataSource create() throws SQLException {
			final DataSource mock = mock(DataSource.class);
			final Connection conn = mock(Connection.class);
			when(mock.getConnection()).thenReturn(conn);
			return mock;
		}

		public void close(@Disposes DataSource ds) {
			// empty
		}
}
