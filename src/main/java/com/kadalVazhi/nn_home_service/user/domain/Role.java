package com.kadalVazhi.nn_home_service.user.domain;

/**
 * Roles supported in the Kadal Vazhi platform.
 *
 * BOAT_OWNER: Owns single/fleet of boats, manages expenses, monitors P&L.
 * CAPTAIN: Commands sea voyages, logs fuel/catch, leads deep sea missions.
 * CREW_MEMBER: Deckhand, driver, mechanic, fishing labor seeking jobs or assigned to voyages.
 * FISHERMAN: Traditional / one-day boat operators.
 * BUYER: Seafood consumers, restaurants, and wholesale buyers pre-ordering catch.
 * ADMIN: System administrators and harbor authority managers.
 */
public enum Role {
    FISHERMAN,
    BOAT_OWNER,
    CAPTAIN,
    CREW_MEMBER,
    BUYER,
    ADMIN
}
