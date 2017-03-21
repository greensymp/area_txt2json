package area_txt2json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Area {

	public int codigo;
	public String nome;
	public JSONArray subareas;

	public Area(int _codigo, String _nome) {
		nome = _nome;
		codigo = _codigo;
		subareas = new JSONArray();
	}

	public void addSubarea(int _codigo, String _nome) {
		JSONObject json = new JSONObject();
		json.put("codigo", _codigo);
		json.put("nome", _nome);
		subareas.put(json);
	}

	public static void main(String[] args) {

		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;

			connection = DriverManager.getConnection("jdbc:sqlite:/Users/gabrielantonio/Desktop/areas/db/areas.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// path

			// String path =
			// "/Users/gabrielantonio/Desktop/areas/ar_engenharia_civil";
			FileFilter filter = new FileFilter() {
			    public boolean accept(File file) {
			        return file.getName().startsWith("ar_");
			    }
			};
			File arquivos[];
			File diretorio = new File("/Users/gabrielantonio/Desktop/areas/txt/");
			arquivos = diretorio.listFiles(filter);
			for (int i = 0; i < arquivos.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(arquivos[i]));
				Area area = new Area(Integer.parseInt(br.readLine()), br.readLine());

				statement.executeUpdate("insert into area values(" + area.codigo + ",'" + area.nome + "')");

				System.out.println("Area: " + area.nome + " com código: " + area.codigo);

				while (br.ready()) {
					String codigo = br.readLine();
					String nome = br.readLine();
					area.addSubarea(Integer.parseInt(codigo), nome);
					statement.executeUpdate(
							"insert into subarea values(" + area.codigo + "," + codigo + ",'" + nome + "')");

				}
				br.close();

				JSONObject json = new JSONObject();
				json.put("nome", area.nome);
				json.put("codigo", area.codigo);
				json.put("subareas", area.subareas);

				System.out.println(json);

				BufferedWriter bw = new BufferedWriter(
						new FileWriter("/Users/gabrielantonio/Desktop/areas/json/" + arquivos[i].getName() + ".json"));
				bw.write(json.toString());
				bw.close();
			}

			connection.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class Subarea {
		public int codigo;
		public String nome;

		public Subarea(int _codigo, String _nome) {
			codigo = _codigo;
			nome = _nome;
		}

		@Override
		public String toString() {
			return "\"nome\":\"" + nome + "\", " + "\"codigo\":\"" + codigo + "\"";
		}
	}
}
