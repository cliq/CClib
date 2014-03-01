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
	public boolean save(String key, byte[] value);

	/**
	 * Load data from source.
	 *
	 * @param key Field name to be loaded.
	 * @return Loaded data. Should return null if failed.
	 */
	public byte[] load(String key);

	/**
	 * Clear data.
	 *
	 * @param key Field name to be cleared.
	 * @return True if data was cleared successfully.
	 */
	public boolean clear(String key);

}
