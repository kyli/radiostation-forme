/*
 * 2009-03-09
 * radiostation-forme
 * 
 * Copyright (C) 2009  Kaiyi Li
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fm.radiostation.ui.action;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.TextField;
import fm.radiostation.UrlFactory;
import fm.radiostation.RSFMSession;
import fm.radiostation.ui.RSFMResource;

public class SettingConfigAction extends AbstractRSFMAction implements DialogClosedListener{

	private PasswordEditField password;
	private TextField username;
	private CheckboxField forceWifi;

	public SettingConfigAction(RSFMSession session) {
		super(session);
	}

	public void run() {
		final ResourceBundle bsrb = RSFMResource.getUiResources();
		final Dialog settings = new Dialog(Dialog.D_YES_NO, bsrb.getString(8), 0,
				null, Dialog.NO_VERTICAL_SCROLLBAR
						| Dialog.NO_HORIZONTAL_SCROLLBAR);
		settings.setDialogClosedListener(this);
		Font font = Font.getDefault().derive(Font.PLAIN, 15);
		LabelField userinfo = new LabelField(bsrb.getString(16));
		userinfo.setFont(font);
		settings.add(userinfo);
		username = new TextField();
		username.setFont(font);
		username.setLabel(bsrb.getString(18)+": ");
		settings.add(username);
		
		password = new PasswordEditField();
		password.setFont(font);
		password.setLabel(bsrb.getString(19)+": ");
		settings.add(password);
		
		forceWifi = new CheckboxField(bsrb.getString(17), UrlFactory.forceWifi);
		forceWifi.setFont(font);
		settings.add(forceWifi);
		
		ButtonField clearAll = new ButtonField(bsrb.getString(20));
		clearAll.setFont(font);
		settings.add(clearAll);
		clearAll.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				Dialog confirm = new Dialog(Dialog.D_OK_CANCEL, bsrb.getString(21),
						0, null, Dialog.DEFAULT_CLOSE);
				confirm.show();
				confirm.setDialogClosedListener(new DialogClosedListener() {
					public void dialogClosed(Dialog dialog, int choice) {
						if (choice == Dialog.OK) {
							settings.close();
							session.cleanup();
						}
					}
				});
			}
		});
		loadSettings();
		settings.show();
	}

	private void loadSettings() {
		String name = session.getUsername();
		username.setText(name == null ? "" : name);
		String pwd = session.getPassword();
		password.setText(pwd == null ? "" : pwd);
	}

	public void dialogClosed(Dialog dialog, int choice) {
		if (choice == Dialog.YES) {
			if (dialog.isDirty()) {
				String name = username.getText();
				String pwd = password.getText();
				UrlFactory.forceWifi = forceWifi.getChecked();
				session.saveSettings(name, pwd);
				session.loadSettings();
				Thread th = new Thread() {
					public void run() {
						session.fetchMobileSession();
						session.handshake();
					}
				};
				th.start();
			}
		}
	}
}
