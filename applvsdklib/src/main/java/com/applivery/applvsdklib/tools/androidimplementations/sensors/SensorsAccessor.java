/*
 * Copyright (c) 2016 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.applivery.applvsdklib.tools.androidimplementations.sensors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class SensorsAccessor {
  private final Map<String, AndroidSensorWrapper> sensors;

  public SensorsAccessor(SensorEventsReceiver sensorEventsReceiver) {
    sensors = new HashMap();
    initSensors(sensorEventsReceiver);
  }

  private void initSensors(SensorEventsReceiver sensorEventsReceiver) {
    //init Accelerometer sensor SHAKE Detection
    sensors.put(AppliverySensorType.ACCELEROMETER.getStringValue(),
        new AndroidSensorWrapper(AppliverySensorType.ACCELEROMETER,
            new ShakeDetector(sensorEventsReceiver)));

    //init Init all required sensors here


  }

  public boolean isSensorAvailable(AppliverySensorType appliverySensorType){
    if (sensors.containsKey(appliverySensorType.getStringValue())){
      AndroidSensorWrapper androidSensorWrapper = sensors.get(appliverySensorType.getStringValue());
      return androidSensorWrapper.isEnabled();
    }else{
      return false;
    }
  }

  public void setSensorAsAvailable(AppliverySensorType appliverySensorType){
    if (!isSensorAvailable(appliverySensorType) &&
        sensors.containsKey(appliverySensorType.getStringValue())){
      AndroidSensorWrapper androidSensorWrapper = sensors.get(appliverySensorType.getStringValue());
      androidSensorWrapper.enableSensor();
    }
  }

  public void enableSensor(int type) {
    AppliverySensorType appliverySensorType = AppliverySensorType.toAppliverySensor(type);
    setSensorAsAvailable(appliverySensorType);
  }

  public boolean hasAvailableSensors() {

    Iterator it = sensors.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      AndroidSensorWrapper androidSensorWrapper = (AndroidSensorWrapper) pair.getValue();
      if (androidSensorWrapper.isEnabled()){
        return true;
      }
    }

    return false;
  }

  public AppliverySensorIterator getIterator() {
    return new AppliverySensorIterator();
  }

  public void disableSensor(int type) {
    AndroidSensorWrapper androidSensorWrapper = getSensorWrapper(type);

    if (androidSensorWrapper!=null){
      androidSensorWrapper.disableSensor();
    }
  }

  public AndroidSensorWrapper getSensor(int type) {
    return getSensorWrapper(type);
  }

  private AndroidSensorWrapper getSensorWrapper(int type) {
    AppliverySensorType appliverySensorType = AppliverySensorType.toAppliverySensor(type);
    if (isSensorAvailable(appliverySensorType) &&
        sensors.containsKey(appliverySensorType.getStringValue())){
      AndroidSensorWrapper androidSensorWrapper = sensors.get(appliverySensorType.getStringValue());
      return androidSensorWrapper;
    }
    return null;
  }

  protected class AppliverySensorIterator {

    private Set<String> keys;
    private Iterator<String> internalIterator;

    public AppliverySensorIterator() {
      this.keys = filterEnabledSensorsKeySet();
      this.internalIterator = keys.iterator();
    }

    private Set<String> filterEnabledSensorsKeySet() {
      Set<String> filteredSet = new HashSet<>();

      for (String s : sensors.keySet()) {
        if (sensors.get(s).isEnabled()){
          filteredSet.add(s);
        }
      }

      return filteredSet;
    }

    public boolean hasNext() {
      return internalIterator.hasNext();
    }

    public AndroidSensorWrapper next() {
      String key = internalIterator.next();
      AndroidSensorWrapper androidSensorWrapper = sensors.get(key);
      return androidSensorWrapper;
    }
  }
}
