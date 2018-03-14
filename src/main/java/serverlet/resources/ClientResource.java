package serverlet.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.server.Uri;

import serverlet.intercept.ClientConfig;

/**
 * @author Michal Gajdos
 */
@Path("client")
public class ClientResource {

	@ClientConfig
	@Uri("server")
	private WebTarget server;

	/**
	 * Resource method is not intercepted itself.
	 *
	 * In the injected client the {@code StringProvider} is registered and the
	 * provider is intercepted (constructor, isReadable).
	 */
	@GET
	public String get() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
		}
		Connection connection = null;
		long updateRes = -1;
		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost/myblog?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useAffectedRows=true",
					"root", "root");
			java.sql.Statement stmt; // ��������
			stmt = connection.createStatement();
			String updateSql = "UPDATE fk_cate SET name = '1'";
			updateRes = stmt.executeUpdate(updateSql);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		System.out.println("������:" + String.valueOf(updateRes));
		return "ClientResource: Invoke request to non-intercepted" + " server resource method\n" + "   "
				+ server.request().get(String.class);
	}
}
