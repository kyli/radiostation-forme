# Introduction #

RadioStation.ForMe (rs.fm) is developed using JDE 4.5.0, therefore should be compatible with all BlackBerry devices v4.5 and up.

## Known Issues ##

RadioStation.ForMe (rs.fm) is tested mostly on BlackBerry Bold. Though it should work on any BlackBerry device v4.5 and up, there are several problems you should be aware of.

Currently rs.fm provides only single layout orientation. For models with narrower/slimmer screens, rs.fm may not fit as beautifully as you would expect. rs.fm is best displayed in landscape orientation (wide screens).

There are known firmware bugs with earlier BlackBerry devices
  * Occasionally, connection to Last.fm server may disconnect and become unresponsive. rs.fm has a Timeout Watcher that kills dead connections and skips to the next track. It does so by first receiving certain notifications from the player, and then determines if the connection is dead after a limited period of time. But in BlackBerry v4.6 and lower devices, `javax.microedition.media.Player` does not fire `bufferingStart` event as expected. Therefore, this mechanism does not work in those devices. Whenever there is a connection error, a user must manually skip the current track

# Detailed List #

RadioStation.ForMe has been tested on following devices and platforms.

Blackberry v4.7:
BlackBerry Bold 9000 (Bold)
BlackBerry Curve 8900 (Javellin)

BlackBerry v4.6:
None

BlackBerry v4.5:
None