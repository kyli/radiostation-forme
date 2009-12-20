/*
 * Feb 2, 2009
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

package fm.radiostation.ui;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import fm.radiostation.RSFMSession;
import fm.radiostation.RSFMUtils;
import fm.radiostation.StatusEvent;
import fm.radiostation.StatusEventListener;
import fm.radiostation.Track;
import fm.radiostation.player.RadioPlayerEvent;
import fm.radiostation.player.RadioPlayerEventListener;
import fm.radiostation.ui.action.AboutAction;
import fm.radiostation.ui.action.AbstractRSFMAction;
import fm.radiostation.ui.action.BuyTrackAction;
import fm.radiostation.ui.action.LoveOrBanTrackAction;
import fm.radiostation.ui.action.PlayOrStopAction;
import fm.radiostation.ui.action.SettingConfigAction;
import fm.radiostation.ui.action.SkipTrackAction;
import fm.radiostation.ui.action.TuneStationAction;

/**
 * The main screen of RadioStation.ForMe. Contains various key UI components,
 * including album cover and playback controls.
 * 
 * @author kaiyi
 * 
 */
public class RSFMBaseScreen extends MainScreen implements StatusEventListener,
RadioPlayerEventListener {

	private static final int MAX_IMAGE_SIZE = 175;

	private final ResourceBundle bsrb;

	private BitmapField albumCover;
	private TextField artistName;
	private TextField albumName;
	private TextField trackName;

	private LabelField status;

	private RSFMMenuItem audioPlaybackControl;
	private RSFMMenuItem skipTrackControl;
	private RSFMMenuItem tuneStationControl;
	private RSFMMenuItem settingControl;
	private RSFMMenuItem aboutControl;

	private final RSFMSession rsfmSession;

	private LabelField timeElapsed;
	private LabelField duration;

	private Bitmap defaultAlbumArt;
	private IconButton playOrStop;
	private Bitmap playIcon;
	private Bitmap stopIcon;

	public RSFMBaseScreen(RSFMSession rsfmSession) {
		super(NO_VERTICAL_SCROLL);
		this.rsfmSession = rsfmSession;
		rsfmSession.addStatusEventListener(this);
		rsfmSession.getRadioPlayer().addRadioPlayerEventListener(this);

		// initialize rsfm actions
		PlayOrStopAction playOrStopAction = new PlayOrStopAction(rsfmSession);
		SkipTrackAction skipTrackAction = new SkipTrackAction(rsfmSession);
		TuneStationAction tuneStationAction = new TuneStationAction(rsfmSession);
		SettingConfigAction settingConfigAction = new SettingConfigAction(rsfmSession);
		AboutAction aboutAction = new AboutAction(rsfmSession);
		LoveOrBanTrackAction loveTrackAction = new LoveOrBanTrackAction(rsfmSession, true);
		LoveOrBanTrackAction banTrackAction = new LoveOrBanTrackAction(rsfmSession, false);
		BuyTrackAction buyTrackAction = new BuyTrackAction(rsfmSession);

		// retrieve resource bundle and set frame title
		bsrb = RSFMResource.getUiResources();
		LabelField title = new LabelField(bsrb.getString(0), LabelField.FIELD_LEFT
				| LabelField.READONLY);
		setTitle(title);

		// ui setup
		MarginalManager mm = new MarginalManager(Manager.NO_HORIZONTAL_SCROLL
				| Manager.NO_VERTICAL_SCROLL, 10);

		HorizontalFieldManager trackDetailContainer = new HorizontalFieldManager();
		defaultAlbumArt = Bitmap
		.getBitmapResource("default_album_cover.png");
		albumCover = new BitmapField(defaultAlbumArt);
		MarginalManager albumContainer = new MarginalManager(Manager.NO_HORIZONTAL_SCROLL
				| Manager.NO_VERTICAL_SCROLL, 5);
		albumContainer.add(albumCover);
		trackDetailContainer.add(albumContainer);

		VerticalFieldManager right1 = new VerticalFieldManager(
				VerticalFieldManager.NO_SCROLL_RESET);
		Font detailsFont = Font.getDefault().derive(Font.PLAIN, 12);
		Font labelFont = detailsFont.derive(Font.PLAIN, 17);

		right1.add(new BitmapField(Bitmap.getBitmapResource("lastfm.png")));

		trackName = new DetailField();
		trackName.setFont(labelFont);
		trackName.setText(bsrb.getString(13));
		trackName.setEditable(false);
		right1.add(trackName);

		albumName = new DetailField();
		albumName.setFont(detailsFont);
		albumName.setText(bsrb.getString(12));
		albumName.setEditable(false);
		LabelField albumNameLabel = new LabelField(bsrb.getString(14)+": ");
		albumNameLabel.setFont(labelFont);
		right1.add(albumNameLabel);
		right1.add(albumName);

		artistName = new DetailField();
		artistName.setFont(detailsFont);
		artistName.setText(bsrb.getString(0));
		artistName.setEditable(false);
		LabelField artistNameLabel = new LabelField(bsrb.getString(15)+": ");
		artistNameLabel.setFont(labelFont);
		right1.add(artistNameLabel);
		right1.add(artistName);

		status = new LabelField("");
		status.setFont(labelFont);
		right1.add(status);
		MarginalManager mm2 = new MarginalManager(Manager.NO_HORIZONTAL_SCROLL
				| Manager.NO_VERTICAL_SCROLL, 3);
		mm2.add(right1);
		trackDetailContainer.add(mm2);

		VerticalFieldManager lower = new VerticalFieldManager(
				VerticalFieldManager.NO_SCROLL_RESET);
		timeElapsed = new LabelField("00:00");
		duration = new LabelField("00:00");
		timeElapsed.setFont(detailsFont);
		duration.setFont(detailsFont);
		HorizontalFieldManager buttonGrp = new HorizontalFieldManager();
		IconButton buy = new IconButton(Bitmap.getBitmapResource("cart.png"), buyTrackAction);
		IconButton skip = new IconButton(Bitmap.getBitmapResource("control_end.png"), skipTrackAction);
		IconButton love = new IconButton(Bitmap.getBitmapResource("heart.png"), loveTrackAction);
		IconButton ban = new IconButton(Bitmap.getBitmapResource("delete.png"), banTrackAction);
		playIcon = Bitmap.getBitmapResource("control_play.png");
		stopIcon = Bitmap.getBitmapResource("control_stop.png");
		playOrStop = new IconButton(playIcon, playOrStopAction);
		buttonGrp.add(playOrStop);
		buttonGrp.add(skip);
		buttonGrp.add(buy);
		buttonGrp.add(love);
		buttonGrp.add(ban);
		BipartBorderManager playTimeBar = new BipartBorderManager(
				Manager.NO_HORIZONTAL_SCROLL | Manager.NO_VERTICAL_SCROLL,
				true, timeElapsed, duration);
		BipartBorderManager controlButtonPanel = new BipartBorderManager(
				Manager.NO_HORIZONTAL_SCROLL | Manager.NO_VERTICAL_SCROLL,
				true, new NullField(), buttonGrp);
		lower.add(controlButtonPanel);
		lower.add(new SeparatorField(SeparatorField.LINE_HORIZONTAL));
		lower.add(playTimeBar);
		BipartBorderManager bbm = new BipartBorderManager(Manager.NO_HORIZONTAL_SCROLL
				| Manager.NO_VERTICAL_SCROLL, false, trackDetailContainer, lower);

		audioPlaybackControl = new RSFMMenuItem(bsrb, 1, 1, 1, playOrStopAction);
		skipTrackControl = new RSFMMenuItem(bsrb, 6, 2, 2, skipTrackAction);
		tuneStationControl = new RSFMMenuItem(bsrb, 3, 3, 3, tuneStationAction);
		settingControl = new RSFMMenuItem(bsrb, 8, 4, 4, settingConfigAction);
		aboutControl = new RSFMMenuItem(bsrb, 7, 5, 5, aboutAction);

		mm.add(bbm);
		add(mm);
		setFocus();
	}

	private class DetailField extends TextField {
		public DetailField() {
			super(NON_FOCUSABLE);
		}

		protected synchronized void paint(Graphics graphics) {
			graphics.setColor(Color.MAROON);
			super.paint(graphics);
		}
	}

	private class IconButton extends BitmapField {
		private AbstractRSFMAction action;

		public IconButton(Bitmap standby, AbstractRSFMAction action) {
			super(standby, BitmapField.FOCUSABLE);
			this.action = action;
		}

		protected boolean navigationClick(int status, int time) {
			action.run();
			return true;
		}
	}

	protected void makeMenu(Menu menu, int instance) {
		menu.add(audioPlaybackControl);
		menu.add(skipTrackControl);
		menu.add(tuneStationControl);
		menu.add(settingControl);
		menu.add(aboutControl);
		super.makeMenu(menu, instance);
	}

	public void close() {
		rsfmSession.shutdownRadio();
		rsfmSession.getRadioPlayer().removeRadioPlayerEventListener(this);
		rsfmSession.removeStatusEventListener(this);
		rsfmSession.saveSession();
		super.close();
	}

	public void statusChanged(final StatusEvent event) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				status.setText(event.getStatus());
			}
		});
	}

	/**
	 * Handles events fired by RadioPlayer, and update the ui where appropriate.
	 * Interested events include {@link RadioPlayerEvent#TRACK_STARTED}, and
	 * {@link RadioPlayerEvent#RADIO_OFF}
	 */
	public void radioPlayerEventOccurred(final RadioPlayerEvent event) {
		if (event.getEvent() == RadioPlayerEvent.TRACK_STARTED) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					if (rsfmSession.getRadioPlayer().isPlaying()) {
						Track tk = event.getTrack();
						timeElapsed.setText("00:00");
						int dur = (int) (rsfmSession.getRadioPlayer().getDuration() / 1000000);
						trackName.setText(tk.getTitle());
						artistName.setText(tk.getCreator());
						duration.setText(RSFMUtils.timeFromSeconds(dur));
						albumName.setText(tk.getAlbum()==null ? "" : tk.getAlbum());
						audioPlaybackControl.setText(bsrb.getString(2));
						playOrStop.setBitmap(stopIcon);
					}
				}
			});
			PlaybackTimer playbackTimer = new PlaybackTimer();
			playbackTimer.run();
			String url = event.getTrack().getImage();
			if (url != null) {
				EncodedImage img = rsfmSession.fetchAlbumArt(url);
				if (img != null) {
					int width = img.getWidth();
					int height = img.getHeight();
					if (height > MAX_IMAGE_SIZE
							|| width > MAX_IMAGE_SIZE) {
						int numerator = Fixed32.toFP(Math.max(width, height));
						int denominator = Fixed32.toFP(MAX_IMAGE_SIZE);
						int scale = Fixed32.div(numerator, denominator);
						img = img.scaleImage32(scale, scale);
					}
					updateAlbumImage(img.getBitmap());
				} else {
					updateAlbumImage(defaultAlbumArt);
				}
			} else {
				updateAlbumImage(defaultAlbumArt);
			}
		} else if (event.getEvent() == RadioPlayerEvent.RADIO_OFF) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					audioPlaybackControl.setText(bsrb.getString(1));
					playOrStop.setBitmap(playIcon);
					trackName.setText(bsrb.getString(13));
					albumName.setText(bsrb.getString(12));
					artistName.setText(bsrb.getString(0));
				}
			});
			updateAlbumImage(defaultAlbumArt);
		}  
	}
	
	private void updateAlbumImage(final Bitmap albumArt) {
		UiApplication.getUiApplication().invokeLater(
				new Runnable() {
					public void run() {
						albumCover.setBitmap(albumArt);
					}
				});
	}

	protected boolean keyControl(char c, int status, int time) {
		if (c == Characters.CONTROL_VOLUME_UP) {
			if (rsfmSession.getRadioPlayer() == null) {
				return false;
			}
			return rsfmSession.getRadioPlayer().incVolume();
		} else if (c == Characters.CONTROL_VOLUME_DOWN) {
			if (rsfmSession.getRadioPlayer() == null) {
				return false;
			}
			return rsfmSession.getRadioPlayer().decVolume();
		}
		return false;
	}

	/**
	 * Timer that schedules tasks that updates the current media time.
	 * <p>
	 * This class makes use of observer pattern, and listens to more higher
	 * level event, RadioPlayerEvent. After it initializes, it updates playback
	 * time until radio stops.
	 */
	private class PlaybackTimer implements RadioPlayerEventListener {
		private Timer timer;

		PlaybackTimer() {
			timer = new Timer();
		}

		public void run() {
			rsfmSession.getRadioPlayer().addRadioPlayerEventListener(PlaybackTimer.this);
			timer.scheduleAtFixedRate(new DurationCounter(), 0, 1000);
		}

		public void radioPlayerEventOccurred(RadioPlayerEvent event) {
			if (event.getEvent() == RadioPlayerEvent.RADIO_OFF
					|| event.getEvent() == RadioPlayerEvent.TRACK_STOPPED) {
				rsfmSession.getRadioPlayer().removeRadioPlayerEventListener(PlaybackTimer.this);
				timer.cancel();
			} 
		}

		/**
		 * Timer task to be executed at each second.
		 */
		private class DurationCounter extends TimerTask {
			public void run() {
				UiApplication.getUiApplication().invokeLater(new Runnable() {
					public void run() {
						if (rsfmSession.getRadioPlayer().isPlaying()) {
							int elapsedTime = (int) (rsfmSession.getRadioPlayer().getCurrentTime() / 1000000);
							int dur = (int) (rsfmSession.getRadioPlayer().getDuration() / 1000000);
							timeElapsed.setText(RSFMUtils.timeFromSeconds(elapsedTime));
							duration.setText(RSFMUtils.timeFromSeconds(dur - elapsedTime));
						}
					}
				});
			}
		}
	}
}
