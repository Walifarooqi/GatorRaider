package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Attacker;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue)
	{

		// useful Nodes and Actors
		Attacker gator = game.getAttacker();
		Node pill = gator.getTargetNode(game.getPillList(), true);
		Node powerPill = gator.getTargetNode(game.getPowerPillList(), true);
		Defender closestDefender = (Defender) (gator.getTargetActor(game.getDefenders(), true));

		// creates Defender array of all defenders in the current game
		Defender defender1 = game.getDefender(0);
		Defender defender2 = game.getDefender(1);
		Defender defender3 = game.getDefender(2);
		Defender defender4 = game.getDefender(3);
		Defender[] defenderArray = new Defender[] {defender1, defender2, defender3, defender4};

		// sorts defenderArray based on distance to attacker
		int a, b;
		Defender temp;
		for (a = 1; a < defenderArray.length; a++){
			b = a;
			while (b > 0 && defenderArray[b].getLocation().getPathDistance(gator.getLocation()) < defenderArray[b - 1].getLocation().getPathDistance(gator.getLocation())){
				temp = defenderArray[b];
				defenderArray[b] = defenderArray[b - 1];
				defenderArray[b - 1] = temp;
				--b;
			}
		}

		// determines how many defenders are in the lair and how many defenders are vulnerable
		int defendersInLair = 0;
		int vulnerableDefenders = 0;
		for (int i = 0; i < defenderArray.length; i++){
			if (defenderArray[i].getLairTime() > 0){
				defendersInLair++;
			}
			if (defenderArray[i].isVulnerable()){
				vulnerableDefenders++;
			}
		}

		// determines how many dangerous defenders are near the attacker
		int numDefendersClose = 0;
		for (int i = 0; i < defenderArray.length; i ++){
			if ((defenderArray[i].getLocation().getPathDistance(gator.getLocation()) < 25) && (defenderArray[i].getLairTime() == 0) && (!defenderArray[i].isVulnerable())){
				numDefendersClose++;
			}
		}

		// makes sure the closest defender is not one that is in the lair
		if (defendersInLair > 0 && defendersInLair < 4){
			closestDefender = defenderArray[defendersInLair];
		}


		if (numDefendersClose > 0) {
			// if a defender is close to the attacker

			int action = gator.getNextDir(closestDefender.getLocation(), false);      // this is a default course of action for the attacker

			if (!(game.getPowerPillList().isEmpty()) && (powerPill.getPathDistance(gator.getLocation()) < powerPill.getPathDistance(closestDefender.getLocation()))){
				// if powerPill exists and is closer to the attacker than the closest defender, then move towards the power pill
				action = gator.getNextDir(powerPill, true);
			}
			else {
				if (!gator.getPathTo(closestDefender.getLocation()).contains(pill) && !gator.getPathTo(defenderArray[1].getLocation()).contains(pill) && !gator.getPathTo(pill).contains(closestDefender.getLocation()) && !gator.getPathTo(pill).contains(defenderArray[1].getLocation())){
					// if path from attacker to next pill does not contain defenders and path from attacker to defenders does not contain that pill, then move towards the pill
					action = gator.getNextDir(pill, true);
				}
			}

			return action;    // this triggers the default course of action for the attacker

		} else if (vulnerableDefenders > 0) {
			// if vulnerable defenders exist
			if ((closestDefender).isVulnerable() && ((closestDefender).getVulnerableTime()) > 10) {
				// if closest vulnerable defender has ample amount of vulnerability time remaining
				if (closestDefender.getLocation().getPathDistance(gator.getLocation()) < 35) {
					// if closest vulnerable defender is within certain range of the attacker
					return gator.getNextDir(closestDefender.getLocation(), true);
				} else {
					// if closest vulnerable defender is out of certain range of the attacker
					return gator.getNextDir(pill, true);
				}
			} else if ((closestDefender).isVulnerable() && ((closestDefender).getVulnerableTime()) < 10) {
				// if there is not enough time to get the next closest vulnerable defender
				if (closestDefender.getLocation().getPathDistance(gator.getLocation()) < 25) {
					// if vulnerable defender is within close range of the attacker, then move away from the defender
					return gator.getNextDir(closestDefender.getLocation(), false);
				} else {
					// default, move towards closest regular pill
					return gator.getNextDir(pill, true);
				}
			} else if (!(closestDefender).isVulnerable()) {
				// if closest defender is not vulnerable
				if (closestDefender.getLocation().getPathDistance(gator.getLocation()) < 25) {
					// if closest defender is within close range, then move away from the defender
					return gator.getNextDir(closestDefender.getLocation(), false);
				} else {
					// default, move towards closest regular pill
					return gator.getNextDir(pill, true);
				}
			} else {
				// default, move towards closest regular pill
				return gator.getNextDir(pill, true);
			}
		} else {
			// default, move towards closest regular pill
			return gator.getNextDir(pill, true);
		}


	}
}