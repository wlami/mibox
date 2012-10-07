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
package com.wlami.mibox.client.networking.synchronization;

import com.wlami.mibox.client.metadata.MChunk;

/**
 * This interface defines a class which utilizes one or more
 * {@link TransportWorker} implementations to execute the network and encryption
 * tasks.
 * 
 * @author Wladislaw Mitzel
 * 
 */
public interface TransportProvider<T extends UploadRequest<?>> {

	/**
	 * Start transport. Each transporter runs in a separate thread.
	 */
	void startProcessing();

	/**
	 * Stop transport. If more than one transporter is active all of them are
	 * shut down.
	 */
	void stopProcessing();

	/**
	 * Add a {@link MChunk} to the upload list of the transporters. After the
	 * upload has been finished the callback is executed.
	 * 
	 * @param mChunkUpload
	 *            instance which defines the {@link MChunk} and a callback
	 *            method.
	 */
	void addChunkUpload(T uploadRequest);

	/**
	 * Add several upload requests by using a container.
	 * 
	 * @param uploadRequestContainer
	 *            The container which contains the request.
	 */
	void addUploadContainer(RequestContainer<T> uploadRequestContainer);

	/**
	 * Add a {@link DownloadRequest} to the list of the transporters. After the
	 * download has been finished the callback is executed.
	 * 
	 * @param downloadRequest The request which shall be downlaoded.
	 */
	void addDownload(DownloadRequest downloadRequest);

	/**
	 * Add several download requests by using a container.
	 * @param downloadRequestContainer The Container which contains the requests.
	 */
	void addDownloadContainer(RequestContainer<DownloadRequest> downloadRequestContainer);
}
