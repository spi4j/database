package fr.pacman.database.validation.core;

import java.util.HashMap;
import java.util.Map;

import org.obeonetwork.dsl.typeslibrary.TypeInstance;

public class TypeUtils {

	private static final String _ppostgres = "|BIT|BIT VARYING|BIGINT|BPCHAR|CHAR|CHARACTER VARYING|CHARACTER|NUMERIC|VARBIT|";

	private static final String _poracle = "|CHAR|INTERVAL DAY(%n) TO SECOND|INTERVAL YEAR (%n) TO MONTH|NCHAR|NUMBER|NVARCHAR2|RAW"
			+ "|TIMESTAMP|TIMESTAMP (%n) WITH LOCAL TIME ZONE|TIMSESTAMP (%n)WITH TIME ZONE|VARCHAR|VARCHAR2|VARCHAR2 (%n BYTE)"
			+ "|VARCHAR2 (%n CHAR)|";

	private static final String _pmysql = "|BIGINT|BINARY|BIT|CHAR|DEC|DECIMAL|DOUBLE|FIXED|FLOAT|INT|INTEGER|MEDIUMINT"
			+ "|NUMERIC|SMALLINT|TIMESTAMP|TINYINT|VARBINARY|VARCHAR|";

	private static final String _pmariaDb = "|BIGINT|BINARY|BIT|CHAR|DEC|DECIMAL|DOUBLE|FIXED|FLOAT|INT|INTEGER|"
			+ "MEDIUMINT|NUMERIC|SMALLINT|TIMESTAMP|TINYINT|VARBINARY|VARCHAR|";

	private static final String _psqlServer = "|BINARY VARYING|BINARY|CHAR VARYING|CHAR|CHARACTER VARYING|CHARACTER|DATETIME2|"
			+ "DATETIMEOFFSET|DEC|DECIMAL|FLOAT|INT|NATIONAL CHAR VARYING|NATIONAL CHAR|VANATIONAL CHARACTER VARYINGR|"
			+ "NATIONAL CHARACTER|NCHAR|NUMERIC|NVARCHAR|TIME|VARBINARY|VARCHAR|";

	private static final String _ph2 = "|BINARY|BLOB|BYTEA|CHAR|CHARACTER|CLOB|DEC|DECIMAL|DOUBLE|IMAGE|INT|LONGBLOB|"
			+ "LONGTEXT|LONGVARBINARY|LONGVARCHAR|MEDIUMBLOB|MEDIUMTEXT|NCHAR|NCLOB|NTEXT|NUMBER|NUMERIC|NVARCHAR|"
			+ "NVARCHAR2|OID|RAW|TEXT|TINYBLOB|TINYTEXT|VARBINARY|VARCHAR|VARCHAR_CASESENSITIVE|VARCHAR_IGNORECASE|"
			+ "VARCHAR2|";

	private static final String _plogical = "|BINARY|DÉCIMAL|ENTIER|ENTIER LONG|RÉEL|TEXTE|";

	/**
	 * Liste des types pour les tailles et précisions.
	 */
	private static Map<String, String> _ptypes;

	static {
		_ptypes = new HashMap<>();
		_ptypes.put("H2", _ph2);
		_ptypes.put("MARIADB", _pmariaDb);
		_ptypes.put("MYSQL", _pmysql);
		_ptypes.put("ORACLE", _poracle);
		_ptypes.put("POSTGRES", _ppostgres);
		_ptypes.put("SQLSERVER", _psqlServer);
		_ptypes.put("LOGICAL MODEL", _plogical);
	}

	/**
	 * 
	 * @param p_type
	 * @param p_dbType
	 * @return
	 */
	public static boolean is_lengthNotNull(final TypeInstance p_type, final String p_dbType) {
		try {
			String types = _ptypes.get(p_dbType.toUpperCase());
			if (null != types) {
				if (types.indexOf('|' + p_type.getNativeType().getName().toUpperCase().trim() + '|') != -1) {
					if (p_type.getLength() == null) {
						return false;
					}
				}
			}
			return true;
		} catch (Exception p_e) {

			return false;
		}
	}

	public static boolean is_lengthNotZero(final TypeInstance p_type, final String p_dbType) {
		try {
			String types = _ptypes.get(p_dbType.toUpperCase());
			if (null != types) {
				if (types.indexOf('|' + p_type.getNativeType().getName().toUpperCase().trim() + '|') != -1) {
					if (p_type.getLength() != null && !(p_type.getLength() > 0)) {
						return false;
					}
				}
			}
			return true;
		} catch (Exception p_e) {

			return false;
		}
	}
}
