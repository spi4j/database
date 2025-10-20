package fr.pacman.database.validation.ui.generators;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.Logger;

import fr.pacman.database.validation.core.PacmanGenerator;
import fr.pacman.database.validation.main.GenValidation;
import fr.pacman.database.validation.ui.core.PacmanUIGenerator;
import fr.pacman.database.validation.ui.plugin.Activator;

/**
 * UI Generateur pour la validation des modèles.
 * 
 * Se reporter à la classe {@link PacmanUIGenerator} pour l'explication des
 * différentes méthodes.
 * 
 * @author MINARM
 */
public class ValidationUIGenerators extends PacmanUIGenerator {

	/**
	 * Constructeur.
	 * 
	 * @param p_selected la ressource sélectionnée (ici obligatoirement un fichier).
	 */
	public ValidationUIGenerators(final IFile p_selected) {
		super(p_selected);
	}

	@Override
	protected PacmanGenerator getGenerator() {
		return new GenValidation();
	}

	@Override
	protected String getPluginId() {
		return Activator.c_pluginId;
	}

	@Override
	protected Logger getLogger() {
		return Activator.getDefault().getPluginLogger();
	}
}
