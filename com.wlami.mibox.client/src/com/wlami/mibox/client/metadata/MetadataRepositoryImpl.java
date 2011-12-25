/**
 *     MiBox Client - folder synchronization client
 *  Copyright (C) 2011 Wladislaw Mitzel
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wlami.mibox.client.metadata;

import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettingsDao;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Named
public class MetadataRepositoryImpl implements MetadataRepository {

	/** internal logging object. */
	private static final Logger log = LoggerFactory
			.getLogger(MetadataRepositoryImpl.class);

	/**
	 * Reference to the {@link AppSettingsDao} bean.
	 */
	AppSettingsDao appSettingsDao;

	/**
	 * reference to a {@link MetadataWorker} instance which handles the file
	 * structure.
	 */
	private MetadataWorker worker;

	/**
	 * set of incoming {@link ObservedFilesystemEvent} instances which shall be
	 * processed by the {@link MetadataWorker}.
	 */
	private ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents = new ConcurrentSkipListSet<ObservedFilesystemEvent>();

	/**
	 * default constructor.
	 */
	@Inject
	public MetadataRepositoryImpl(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.metadata.MetadataRepository#start()
	 */
	@Override
	public void startProcessing() {
		if (worker == null) {
			worker = new MetadataWorker(appSettingsDao, incomingEvents);
			worker.start();
			log.info("Starting MetadataRepository");
		} else {
			log.debug("MetadataRepository already started.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.metadata.MetadataRepository#stop()
	 */
	@Override
	public void stopProcessing() {
		log.info("Stopping MetadataRepository...");
		worker.setActive(false);
		try {
			worker.join();
		} catch (InterruptedException e) {
			log.warn(e.getMessage());
		}
		log.info("MetadataRepository stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.metadata.MetadataRepository#addEvent(com.wlami
	 * .mibox.client.metadata.ObservedFilesystemEvent)
	 */
	@Override
	public void addEvent(ObservedFilesystemEvent observedFilesystemEvent) {
		if (!incomingEvents.add(observedFilesystemEvent)) {
			log.debug("ObservedFilesystemEvent not added - already existing");
		}
	}

}
