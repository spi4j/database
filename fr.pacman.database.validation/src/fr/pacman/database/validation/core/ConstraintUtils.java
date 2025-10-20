package fr.pacman.database.validation.core;

import org.obeonetwork.dsl.database.Column;
import org.obeonetwork.dsl.database.Constraint;
import org.obeonetwork.dsl.database.Table;

/**
 * 
 * @author patrice.garaud
 */
public class ConstraintUtils {

	/**
	 * 
	 * @param p_constraint
	 * @param p_table
	 * @return
	 */
	public static boolean has_validColumns(final Constraint p_constraint, final Table p_table) {
		for (Column column : p_table.getColumns()) {
			if (p_constraint.getExpression().toUpperCase().indexOf(column.getName().toUpperCase().trim()) != -1)
				return true;
		}
		return false;
	}
}
