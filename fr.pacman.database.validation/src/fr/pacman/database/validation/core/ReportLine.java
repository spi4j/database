package fr.pacman.database.validation.core;

import org.eclipse.emf.ecore.EObject;

public class ReportLine {

	private String _msg;
	private EObject _emfObject;

	ReportLine(final EObject p_object, final String p_msg) {
		_emfObject = p_object;
		_msg = p_msg;

	}

	public String getMsg() {
		return _msg;
	}

	public EObject getEmfObject() {
		return _emfObject;
	}
}
