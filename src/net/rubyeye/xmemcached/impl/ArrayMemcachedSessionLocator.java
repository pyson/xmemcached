/**
 *Copyright [2009-2010] [dennis zhuang(killme2008@gmail.com)]
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License. 
 *You may obtain a copy of the License at 
 *             http://www.apache.org/licenses/LICENSE-2.0 
 *Unless required by applicable law or agreed to in writing, 
 *software distributed under the License is distributed on an "AS IS" BASIS, 
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 *either express or implied. See the License for the specific language governing permissions and limitations under the License
 */
package net.rubyeye.xmemcached.impl;

import java.util.List;

import net.rubyeye.xmemcached.HashAlgorithm;
import net.rubyeye.xmemcached.MemcachedSessionLocator;
import net.rubyeye.xmemcached.MemcachedTCPSession;

/**
 * 基于余数的分布查找
 * 
 * @author dennis
 * 
 */
public class ArrayMemcachedSessionLocator implements MemcachedSessionLocator {
	protected HashAlgorithm hashAlgorighm;
	List<MemcachedTCPSession> sessions;

	public ArrayMemcachedSessionLocator() {
		this.hashAlgorighm = HashAlgorithm.NATIVE_HASH;
	}

	public ArrayMemcachedSessionLocator(HashAlgorithm hashAlgorighm) {
		this.hashAlgorighm = hashAlgorighm;
	}

	public long getHash(String key) {
		long hash = hashAlgorighm.hash(key);
		return hash % sessions.size();
	}

	@Override
	public MemcachedTCPSession getSessionByKey(String key) {
		if (sessions.size() == 0)
			return null;
		long mod = getHash(key);
		int findCount = 0;
		Label: while (findCount < 10) {
			MemcachedTCPSession session = sessions.get((int) mod);
			findCount++;
			session = sessions.get((int) mod);
			if (session == null || session.isClose()) {
				mod = (mod > sessions.size() - 1) ? 0 : mod + 1;
				break Label;
			} else
				return session;
		}
		return null;
	}

	@Override
	public void setSessionList(List<MemcachedTCPSession> list) {
		sessions = list;
	}
}