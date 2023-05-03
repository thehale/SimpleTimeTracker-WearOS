<!-- This Source Code Form is subject to the terms of the Mozilla Public
   - License, v. 2.0. If a copy of the MPL was not distributed with this
   - file, You can obtain one at https://mozilla.org/MPL/2.0/. -->

<div align="center">

# Simple Time Tracker for Wear OS

<!-- BADGES -->
[![](https://badgen.net/github/license/thehale/SimpleTimeTracker-WearOS)](https://github.com/thehale/SimpleTimeTracker-WearOS/blob/master/LICENSE)
[![](https://badgen.net/badge/icon/Sponsor/pink?icon=github&label)](https://github.com/sponsors/thehale)
[![](https://badgen.net/badge/icon/Follow%20@jhaledev/1DA1F2?icon=twitter&label)](https://twitter.com/intent/user?screen_name=jhaledev)

|                                                                         |                                                                          |
| ----------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| ![Application screenshot](docs/simpletimetracker-wearos-list-start.png) | ![Application screenshot](docs/simpletimetracker-wearos-list-middle.png) |

</div>


A WearOS companion app to trigger time tracking events from your wrist. Designed for use with 
[SimpleTimeTracker](https://github.com/Razeeman/Android-SimpleTimeTracker) version 1.20 or later.

NOTE: This application is currently hard-coded to the specific set of activities and tags defined in
[`TimeTrackingActivity.kt`](./wear/src/main/java/dev/jhale/android/wear/simpletimetracker/data/TimeTrackingActivity.kt).
To use this application with different activities/tags you will have to edit that file, recompile,
and install on your devices.

## Resources Referenced During Developement
 - [Compose for Wear OS Codelab](https://developer.android.com/codelabs/compose-for-wear-os#0)
 - [Send and receive messages](https://developer.android.com/training/wearables/data/messages)
 - [Handle data layer events](https://developer.android.com/training/wearables/data/events#Listen)
 - [Get Wear OS and Android Talking](https://code.tutsplus.com/tutorials/get-wear-os-and-android-talking-exchanging-information-via-the-wearable-data-layer--cms-30986)
 - [Integrating rotary input](https://github.com/joreilly/PeopleInSpace/pull/84/files)

## License
Mozilla Public License 2.0
