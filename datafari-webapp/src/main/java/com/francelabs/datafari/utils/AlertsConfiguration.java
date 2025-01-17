/*******************************************************************************
 * Copyright 2015 France Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.francelabs.datafari.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.francelabs.datafari.config.AbstractConfigClass;

/**
 * Configuration reader
 *
 * @author France Labs
 *
 */
public class AlertsConfiguration extends AbstractConfigClass {

  // Properties frequencies
  public final static String HOURLY_DELAY = "HOURLYDELAY";
  public final static String DAILY_DELAY = "DAILYDELAY";
  public final static String WEEKLY_DELAY = "WEEKLYDELAY";
  public final static String LAST_HOURLY_EXEC = "Hourly";
  public final static String LAST_DAILY_EXEC = "Daily";
  public final static String LAST_WEEKLY_EXEC = "Weekly";
  public final static String ALERTS_ON_OFF = "ALERTS";

  // Properties mails
  public final static String SMTP_ADDRESS = "smtp";
  public final static String SMTP_FROM = "from";
  public final static String SMTP_USER = "user";
  public final static String SMTP_PASSWORD = "pass";

  // Properties Database
  public final static String DATABASE_HOST = "HOST";
  public final static String DATABASE_PORT = "PORT";
  public final static String DATABASE_NAME = "DATABASE";
  public final static String DATABASE_COLLECTION = "COLLECTION";

  private static final String configFilename = "alerts.properties";

  private static AlertsConfiguration instance;

  private final static Logger LOGGER = LogManager.getLogger(AlertsConfiguration.class.getName());

  /**
   * Set a property and save it the alerts.properties
   *
   * @param key
   *          : the key that should be change
   * @param value
   *          : the new value of the key
   * @return : true if there's an error and false if not
   */
  @Override
  public void setProperty(final String key, String value) {
    lock.writeLock().lock();
    if (key.equals(SMTP_PASSWORD)) {
      try {
        value = ObfuscationTool.obfuscate(value);
      } catch (final Exception e) {
        LOGGER.error(e);
      }
    }
    properties.setProperty(key, value);
    lock.writeLock().unlock();
  }

  @Override
  public String getProperty(final String key) {
    lock.readLock().lock();
    final String result = (String) properties.get(key);
    if (result == null) {
      LOGGER.warn("Property " + key + " not found in the following property file: " + this.configPropertiesFileNameAbsolutePath);
    }
    lock.readLock().unlock();
    if (key.equals(SMTP_PASSWORD) && result != null) {
      try {
        return ObfuscationTool.deobfuscate(result);
      } catch (final Exception e) {
        LOGGER.error(e);
        return result;
      }
    } else {
      return result;
    }
  }

  /**
   *
   * Get the instance
   *
   */
  public static synchronized AlertsConfiguration getInstance() {
    if (null == instance) {
      instance = new AlertsConfiguration();
    }
    return instance;
  }

  private AlertsConfiguration() {
    super(configFilename, LOGGER);
  }

}