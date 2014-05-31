package com.cliqconsulting.cclib.framework.persistence;

/**
 * IPersistenceMethod
 * <p/>
 * Created by Flavio Ramos on 2/20/14 16:05.
 * Copyright (c) 2013. All rights reserved.
 */
public interface IPersistenceMethod {

	/**
	 * Save data to source.
	 *
	 * @param key   Field name to be saved.
	 * @param value Value to be saved.
	 * @return True if data was saved successfully.
	 */
	public boolean saveBytes(String key, byte[] value);

	public boolean saveString(String key, String value);

	public boolean saveInt(String key, int value);

	boolean saveLong(String key, long value);

	public boolean saveFloat(String key, float value);

	public boolean saveBoolean(String key, boolean value);

	/**
	 * Load data from source.
	 *
	 * @param key Field name to be loaded.
	 * @return Loaded data. Should return null if failed.
	 */
	public byte[] loadBytes(String key);

	public String loadString(String key);

	public int loadInt(String key);

	public float loadFloat(String key);

	public boolean loadBoolean(String key);

	/**
	 * Clear data.
	 *
	 * @param key Field name to be cleared.
	 * @return True if data was cleared successfully.
	 */
	public boolean clear(String key);

	public long loadLong(String gameStateSaveTime);
}
