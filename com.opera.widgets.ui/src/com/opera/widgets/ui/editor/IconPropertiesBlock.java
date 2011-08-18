/*******************************************************************************
 * Copyright (c) 2010-2011 Opera Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Opera Software - initial API and implementation
 *******************************************************************************/

package com.opera.widgets.ui.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.opera.widgets.core.widget.Icon;
import com.opera.widgets.ui.WidgetsActivator;
import com.opera.widgets.ui.editor.contentprovider.IconsContentProvider;
import com.opera.widgets.ui.editor.dialog.IconWizard;
import com.opera.widgets.ui.editor.labelprovider.IconsLabelProvider;

/**
 * Properties block of Icons tab in Config Editor. Contains list of icons
 * associated with particular widget project
 * 
 * @author Michal Borek
 * @see IconsPage
 * 
 */
public class IconPropertiesBlock extends MasterDetailsBlock {

    public static final String[] LEGAL_EXTENSIONS = {
	    "gif", "ico", "jpeg", "jpg", "png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	    "svg" }; //$NON-NLS-1$
    private static final String SRC_PATH = "src/"; //$NON-NLS-1$
    private IconsPage fFormPage;
    private TableViewer fViewer;

    public IconPropertiesBlock(IconsPage formPage) {
	this.fFormPage = formPage;
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm,
	    Composite parent) {
	FormToolkit toolkit = managedForm.getToolkit();
	Section section = toolkit.createSection(parent, Section.TITLE_BAR);
	section.setText(Messages.OverviewPage_TitleIcons); //$NON-NLS-1$
	section.marginWidth = 10;
	section.marginHeight = 5;
	Composite client = toolkit.createComposite(section, SWT.WRAP);
	GridLayout layout = new GridLayout();
	layout.numColumns = 2;
	layout.marginWidth = 2;
	layout.marginHeight = 2;
	client.setLayout(layout);
	Table t = toolkit.createTable(client, SWT.NULL);
	GridData gd = new GridData(GridData.FILL_BOTH);
	gd.heightHint = 20;
	gd.widthHint = 100;
	gd.verticalSpan = 2;
	t.setLayoutData(gd);
	toolkit.paintBordersFor(client);

	Button addButton = toolkit.createButton(client,
		Messages.OverviewPage_ButtonAdd, SWT.PUSH);
	gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	gd.widthHint = 90;
	addButton.setLayoutData(gd);
	addButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleAddIcon();
	    }
	});

	Button removeButton = toolkit.createButton(client,
		Messages.OverviewPage_ButtonRemove, SWT.PUSH);
	gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	gd.widthHint = 90;
	removeButton.setLayoutData(gd);
	removeButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleDelete();
	    }
	});

	section.setClient(client);
	final SectionPart spart = new SectionPart(section);
	managedForm.addPart(spart);

	fViewer = new TableViewer(t);
	fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
	    public void selectionChanged(SelectionChangedEvent event) {
		managedForm.fireSelectionChanged(spart, event.getSelection());
	    }
	});
	fViewer.setContentProvider(new IconsContentProvider());
	fViewer.setLabelProvider(new IconsLabelProvider(getFormPage()
		.getProject()));
	fViewer.setInput(this.fFormPage.getContent());
	int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
	DropTarget target = new DropTarget(fViewer.getTable(), operations);

	final FileTransfer fileTransfer = FileTransfer.getInstance();
	Transfer[] types = new Transfer[] { fileTransfer };
	target.setTransfer(types);
	target.addDropListener(new IconDropTargetListener(fileTransfer));

    }

    @Override
    protected void registerPages(DetailsPart detailsPart) {
	detailsPart.registerPage(Icon.class, new IconDetailsPage(this));

    }

    @Override
    protected void createToolBarActions(IManagedForm managedForm) {

    }

    public IconsPage getFormPage() {
	return fFormPage;
    }

    public TableViewer getViewer() {
	return fViewer;
    }

    public void handleDelete() {
	if (!fViewer.getSelection().isEmpty()) {
	    Icon iconToRemove = (Icon) ((StructuredSelection) fViewer
		    .getSelection()).getFirstElement();
	    fFormPage.getWidget().remove(iconToRemove);
	    fViewer.getTable().remove(fViewer.getTable().getSelectionIndex());
	    fViewer.getTable().select(0);
	    detailsPart.selectionChanged(detailsPart, fViewer.getSelection());
	}
    }

    private void handleAddIcon() {
	IconWizard wizard = new IconWizard();
	WizardDialog dialog = new WizardDialog(WidgetsActivator.getDefault()
		.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	dialog.create();
	dialog.open();
	if (dialog.getReturnCode() == Status.OK) {
	    fFormPage.getWidget().add(wizard.getIcon());
	    fViewer.refresh();
	}
    }

    private final class IconDropTargetListener implements DropTargetListener {
	private final FileTransfer fileTransfer;

	private IconDropTargetListener(FileTransfer fileTransfer) {
	    this.fileTransfer = fileTransfer;
	}

	public void dragEnter(DropTargetEvent event) {
	    if (event.detail == DND.DROP_DEFAULT) {
		if ((event.operations & DND.DROP_COPY) != 0) {
		    event.detail = DND.DROP_COPY;
		} else {
		    event.detail = DND.DROP_NONE;
		}
	    }
	    // will accept text but prefer to have files dropped
	    for (int i = 0; i < event.dataTypes.length; i++) {
		if (fileTransfer.isSupportedType(event.dataTypes[i])) {
		    event.currentDataType = event.dataTypes[i];
		    // files should only be copied
		    if (event.detail != DND.DROP_COPY) {
			event.detail = DND.DROP_NONE;
		    }
		    break;
		}
	    }
	}

	public void dragOver(DropTargetEvent event) {
	    event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;

	}

	public void dragOperationChanged(DropTargetEvent event) {
	    if (event.detail == DND.DROP_DEFAULT) {
		if ((event.operations & DND.DROP_COPY) != 0) {
		    event.detail = DND.DROP_COPY;
		} else {
		    event.detail = DND.DROP_NONE;
		}
	    }
	    // allow text to be moved but files should only be copied
	    if (fileTransfer.isSupportedType(event.currentDataType)) {
		if (event.detail != DND.DROP_COPY) {
		    event.detail = DND.DROP_NONE;
		}
	    }
	}

	public void dragLeave(DropTargetEvent event) {
	}

	public void dropAccept(DropTargetEvent event) {
	}

	public void drop(DropTargetEvent event) {

	    // Adding icon during drag and drop operation
	    if (fileTransfer.isSupportedType(event.currentDataType)) {
		String[] files = (String[]) event.data;
		IProject project = getFormPage().getProject();
		if (project != null) {
		    String projectPath = project.getLocation().toOSString();
		    for (int i = 0; i < files.length; i++) {
			String file = files[i];
			if (file.startsWith(projectPath)) {
			    String path = files[i].substring(project
				    .getFolder(SRC_PATH).getLocation()
				    .toOSString() //$NON-NLS-1$
				    .length() + 1);
			    addIcon(project, path);
			} else {
			    File filesystemFile = new File(file);
			    String fileName = filesystemFile.getName();
			    IFile newIconFile = project.getFolder(SRC_PATH)
				    .getFile(fileName);
			    if (!newIconFile.exists()
				    || confirmReplace(fileName)) {
				try {
				    if (newIconFile.exists()) {
					newIconFile.delete(true, null);
				    }
				    newIconFile.create(
					    new FileInputStream(file), true,
					    null);
				    addIcon(project, fileName);
				} catch (FileNotFoundException e) {
				    e.printStackTrace();
				} catch (CoreException e) {
				    e.printStackTrace();
				}
			    }
			}
		    }
		}
	    }
	}

	private boolean confirmReplace(String fileName) {
	    return MessageDialog.openConfirm(getShell(), Messages.IconPropertiesBlock_IconExistsErrorTitle, NLS
		    .bind(Messages.IconPropertiesBlock_IconExistsErrorDesc,
			    fileName));
	}

	private void addIcon(IProject project, String path) {
	    IFile iconFile = project.getFile(SRC_PATH + path);
	    if (iconFile.exists()
		    && Arrays.binarySearch(LEGAL_EXTENSIONS,
			    iconFile.getFileExtension()) >= 0) {
		Icon icon = new Icon();
		icon.setPath(path);
		if (!fFormPage.getWidget().getIcons().contains(icon)) {
		    getFormPage().getWidget().add(icon);
		    getFormPage().updateEditor();
		} else {
		    MessageDialog
			    .openWarning(
				    getShell(),
				    Messages.IconPropertiesBlock_IconExistsTitle,
				    NLS.bind(
					    Messages.IconPropertiesBlock_IconExistsDesc,
					    icon.getPath()));
		}
	    }
	}

	private Shell getShell() {
	    return WidgetsActivator.getDefault().getWorkbench()
		    .getActiveWorkbenchWindow().getShell();
	}
    }

}
