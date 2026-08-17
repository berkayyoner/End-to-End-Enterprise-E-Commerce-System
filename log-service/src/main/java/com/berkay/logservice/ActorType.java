package com.berkay.logservice;

/**
 * RULES.md tracks two distinct audiences: public-application accounts ("Users", which
 * includes customers and sellers) and internal "Personnel". SYSTEM covers events with no
 * human actor (scheduled jobs, startup housekeeping).
 */
public enum ActorType {
	USER,
	PERSONNEL,
	SYSTEM
}
