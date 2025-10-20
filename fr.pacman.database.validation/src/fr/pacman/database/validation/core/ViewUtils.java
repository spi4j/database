package fr.pacman.database.validation.core;

import java.util.Collections;
import java.util.List;

import org.obeonetwork.dsl.database.Column;
import org.obeonetwork.dsl.database.Table;
import org.obeonetwork.dsl.database.View;

import net.sf.jsqlparser.JSQLParserException;

/**
 * Valideur de requêtes (comme ce sont des vues, on a obligatoirement que des
 * selects).
 * 
 * @author patrice.garaud
 */
public class ViewUtils {

	static ViewContentProvider _viewProvider;

	public static boolean is_requestParsed(final View p_view) {
		_viewProvider = new ViewContentProvider();
		if (p_view.getQuery() == null || p_view.getQuery().trim().isEmpty())
			return false;
		try {
			_viewProvider.parseViewQuery(p_view.getQuery().trim());
		} catch (JSQLParserException e) {
			return false;
		}
		return true;
	}

	public static List<String> get_parsedTableNames(final View p_view) {
		return (is_requestParsed(p_view)) ? _viewProvider.getTables() : Collections.emptyList();
	}

	/**
	 * Code complètement foireux et incomplet, il semble y avoir un 'bug' au niveau
	 * de jsqlparser qui n'arrive même pas à attacher la table à la colonne. On
	 * essaye de retrouver la table à minima...mais absolument pas précis on
	 * s'arrete à la première table qui a la colonne.
	 * 
	 * @param p_view
	 * @param p_tTables
	 * @return
	 */
	public static boolean has_validColumns(final View p_view, final List<Table> p_tables) {
		if (is_requestParsed(p_view)) {
			for (ColObject column : _viewProvider.getColumns()) {
				completeWithTable(column, p_tables);
				if (column.getTable() == null) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static void completeWithTable(ColObject p_column, final List<Table> p_tables) {
		for (Table table : p_tables) {
			for (Column col : table.getColumns()) {
				if (col.getName().equalsIgnoreCase(p_column.getName())) {
					p_column.setTable(table.getName());
				}
			}
		}
	}
}
