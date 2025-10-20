package fr.pacman.database.validation.ui.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Logger;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import fr.pacman.database.validation.core.PacmanGenerator;
import fr.pacman.database.validation.core.PacmanValidatorsReport;
import fr.pacman.database.validation.core.ReportLine;

/**
 * Classe abstraite pour l'ensemble de générateurs Pacman (au niveau de la
 * couche UI). Cette classe est chargée de l'instanciation et du lancement des
 * différents générateurs internes (hors couche UI) à partir des handlers de la
 * couche UI, handlers eux mêmes activés à partir des fichiers plugin.xml
 * présents dans la couche UI.
 * 
 * Tous les générateurs internes des projets de génération au niveau de la
 * couche UI doivent obligatoirement étendre de cette classe abstraite;
 * 
 * @author MINARM
 */
public abstract class PacmanUIGenerator {

	/**
	 * L'identifiant de la vue pour afficher la liste des erreurs rencontrées au
	 * niveau du diagramme de modélisation.
	 */
	private final static String VIEW_ID = "fr.pacman.database.validation.ui.core.ValidationView";

	/**
	 * Le chemin racine pour le projet, il est déduit de la ressource qui a été
	 * préalablement sélectionnée par l'utilisateur afin de lancer le générateur UI.
	 * Ce chemin sert de base pour le calcul de l'ensemble des différents chemins
	 * cibles de génération.
	 */
	private File _rootPath;

	/**
	 * La liste des ressources sélectionnées par l'utilisateur pour lancer la
	 * génération. Ces ressources sont ici uniquement des {@link EObject}. Si la
	 * ressource est un fichier, alors cette liste est vide.
	 */
	private List<EObject> _values;

	/**
	 * Lec chemin relatif utilisé pour créer le chemin de chargement du fichier
	 * contenenant l'ensemble des représentations (représentations.aird).
	 */
	private String _representations;

	/**
	 * La liste des ressources sélectionnées par l'utilisateur pour lancer la
	 * génération. Ces resources représentent ici uniquement des ressources de type
	 * fichier. Si la génération. Ces ressources sont ici uniquement des
	 * {@link EObject}. Si la ressource est un fichier, alors cette liste est vide.
	 */
	private List<String> _resources;

	/**
	 * Constructeur pour une sélection par ressource de type 'fichier'. Ce fichier
	 * peut être un fichier de type '.entities', '.soa', '.requirements',
	 * .environment'.
	 * 
	 * A ce niveau et pour l'instant on ne prend en compte qu'une seule ressource,
	 * même si le système est prévu à la base pour pouvoir traiter plusieurs
	 * ressources (évolution future si besoin).
	 * 
	 * @param p_selectedResource la ressource sélectionnée par le développeur.
	 * @throws CoreException
	 */
	public PacmanUIGenerator(IResource p_selectedResource) {
		_resources = new ArrayList<>();
		_resources.add(p_selectedResource.getLocation().toString());
		_rootPath = new File(p_selectedResource.getLocation().toString()).getParentFile();
		_representations = File.separator + _rootPath.getName() + File.separator + "representations.aird";
		_values = Collections.emptyList();
	}

	/**
	 * Retourne le générateur à executer pour la demande de génération de code.
	 *
	 * @return le générateur à executer.
	 */
	protected abstract PacmanGenerator getGenerator();

	/**
	 * Retourne l'identifiant unique du plugin, sous forme de chaîne de caractères.
	 * 
	 * @return l'identifiant unique du plugin.
	 */
	protected abstract String getPluginId();

	/**
	 * Retourne le logger spécifique pour le pugin.
	 * 
	 * @return le logger pour le plugin.
	 */
	protected abstract Logger getLogger();

	/**
	 * Méthode principale, point d'entrée pour les générateurs au niveau de la
	 * couche UI. Sur certains générateur, une pré-validation du modèle est
	 * effectuée, on stoppe la génération si le rapport de validation du modèle a
	 * retourné des erreurs de modélisation. Le rapport est visible au niveau de la
	 * console 'ErrorLog' et/ou dans un fichier présent au niveau dyu projet de
	 * modélisation.
	 * 
	 * La validation est elle même un générateur qui peut être rajouté (ou non) au
	 * niveau de la couche UI. Elle est toujours exécutée en premier, le premier
	 * tests est donc toujours passant, en cas d'echec, on sort de la boucle.
	 * 
	 * Lance l'ensemble des générateurs internes qui ont préalablement été
	 * enregistrés auprès du générateur de la couche UI (en l'occurence, l'ensemble
	 * des classes filles de la classe {@link PacmanUIGenerator}).
	 */
	public void generate() {
		final IRunnableWithProgress operation = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor p_monitor) {
				Monitor monitor = new BasicMonitor();
				PacmanGenerator generator = getGenerator();
				generator.setRootPath(_rootPath.getPath());
				generator.setResources(_resources);
				generator.setValues(_values);
				generator.generate(monitor);
			}
		};

		try {
			PlatformUI.getWorkbench().getProgressService().run(true, true, operation);
			if (PacmanValidatorsReport.hasReport())
				displayAndfillReportView();
			else
				eraseReportView();

		} catch (final Exception p_e) {
			PlugInUtils.displayError("Erreur de validation", p_e.getMessage());
		}
	}

	/**
	 * 
	 * @throws PartInitException
	 */
	private void eraseReportView() throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(VIEW_ID);
		if (null != viewPart && viewPart instanceof PacmanUIValidationView validationView)
			validationView.setRows(Collections.emptyList());
		PacmanUIGeneratorHelper.displayPopUpInfo("Le fichier de modélisation est valide.");
	}

	/**
	 * 
	 * @throws PartInitException
	 */
	private void displayAndfillReportView() throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.showView(VIEW_ID);

		List<ValidationRow> rows = new ArrayList<>();
		for (ReportLine errLine : PacmanValidatorsReport.reportForView()) {
			String[] errCols = errLine.getMsg().split("@");
			rows.add(new ValidationRow(errCols[0], errCols[1], errCols[2], errCols[3], errLine.getEmfObject()));
		}
		IViewPart viewPart = page.findView(VIEW_ID);
		if (null != viewPart && viewPart instanceof PacmanUIValidationView validationView) {
			validationView.setRepresentations(_representations);
			validationView.setLinkingEnabled(page);
			validationView.setRows(rows);

			PacmanUIGeneratorHelper.displayPopUpAlert(
					"Le rapport a remonté des erreurs de validation. " + "\nConsultez la vue contenant le rapport.");
		} else {
			PacmanUIGeneratorHelper
					.displayPopUpAlert("Impossible de trouver la vue pour afficher l'ensemble des informations.");
		}
	}
}
