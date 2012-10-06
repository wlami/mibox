/**
 *     MiBox Client - folder synchronization client
 *  Copyright (C) 2012 Stefan Baust
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

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Stefan Baust
 * 
 */
public class DownloadRequestContainer {

	Set<DownloadRequest> completedChildren;

	Set<DownloadRequest> incompleteChildren;

	TransportCallback allChildrenCompletedCallback;

	/**
	 * 
	 */
	public DownloadRequestContainer() {
		completedChildren = new ConcurrentSkipListSet<DownloadRequest>();
		incompleteChildren = new ConcurrentSkipListSet<DownloadRequest>();
	}
	
	/**
	 * Creates a Download container for chunks/other downloads which belong together
	 * @param downloads A collection of downloads
	 * @param childrenCompletedCallback A callback 
	 */
	public DownloadRequestContainer(Collection<DownloadRequest> downloads, TransportCallback childrenCompletedCallback) {
		this.incompleteChildren  = new ConcurrentSkipListSet<>(downloads);
		this.completedChildren = new ConcurrentSkipListSet<>();
		this.allChildrenCompletedCallback = childrenCompletedCallback;
	}

	
	
	/**
	 * @param e
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(DownloadRequest e) {
		return incompleteChildren.add(e);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends DownloadRequest> c) {
		return incompleteChildren.addAll(c);
	}
	
	/**
	 * @param allChildrenCompletedCallback the allChildrenCompletedCallback to set
	 */
	public void setAllChildrenCompletedCallback(
			TransportCallback allChildrenCompletedCallback) {
		this.allChildrenCompletedCallback = allChildrenCompletedCallback;
	}
	
	public void oneChildCompleted(DownloadRequest downloadRequest) {
		if (incompleteChildren.remove(downloadRequest) ) {
			completedChildren.add(downloadRequest);
			if (incompleteChildren.isEmpty()) {
				this.allChildrenCompletedCallback.transportCallback(null);
			}
		}
	}
	
	/**
	 * @return the incompleteChildren
	 */
	public Set<DownloadRequest> getDownloadRequests() {
		return new ConcurrentSkipListSet<>(incompleteChildren);
	}

}