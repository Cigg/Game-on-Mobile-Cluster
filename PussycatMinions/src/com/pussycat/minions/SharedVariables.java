package com.pussycat.minions;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

public class SharedVariables {
	
	private static SharedVariables theOnlyInstance = null;
	private static Object theOnlyInstanceMutex = new Object();
	
	
	private GLOBAL_STATE__ internalState;
	private Object internalStateMutex = new Object();
	
	private float reciveDelay;
	private Object reciveDelayMutex = new Object();
	
	private float sendDelay;
	private Object sendDelayMutex = new Object();
	
	private AtomicInteger nClocks;	
	
	private float deviceAngle;
	private Object deviceAngleMutex = new Object();
	
	private float deviceMiddleX;
	private Object deviceMiddleXMutex = new Object();
	
	private float deviceMiddleY;
	private Object deviceMiddleYMutex = new Object();

	private float mainMiddleX;
	private Object mainMiddleXMutex = new Object();
	
	private float mainMiddleY;
	private Object mainMiddleYMutex = new Object();
	
	private float middleAngle;
	private Object middleAngleMutex = new Object();
	
	private AtomicInteger totalPoints;	
	private AtomicBoolean updatedPoints;
	private AtomicBoolean isRunning;
	private AtomicBoolean mapDone;
	private AtomicBoolean isRemapping;
	private AtomicBoolean startGame;
	private AtomicInteger numberOfPlayers;
	
	private ArrayList<Server> servers = new ArrayList<Server>();
	private Server server = null;
	
	private AtomicInteger gameTimeInSeconds = new AtomicInteger(120);

	ConcurrentHashMap<Integer, Integer> idAndColors = new ConcurrentHashMap<Integer, Integer>();
	ConcurrentHashMap<Integer, Integer> idAndPoints = new ConcurrentHashMap<Integer, Integer>();
	ArrayList<ShortPair> finalScores = new ArrayList<ShortPair>();
	AtomicInteger deviceId = new AtomicInteger(-2);
	

	// Singleton design pattern
	public static SharedVariables getInstance() {
		if( theOnlyInstance == null ) {
			synchronized( theOnlyInstanceMutex ) {
				if( theOnlyInstance == null ) {
					theOnlyInstance = new SharedVariables();
				}
			}
		}
		return theOnlyInstance;
	}
	
	
	private SharedVariables() {
		initialize();
	}
	
	
	public void initialize() {
		setInternalState(GLOBAL_STATE__.START);

		setRecivieDelay(0.0f);
		setSendDelay(0.0f);
		
		nClocks = new AtomicInteger(0);
		updatedPoints = new AtomicBoolean(false);
		isRunning = new AtomicBoolean(false);
		mapDone = new AtomicBoolean(false);
		isRemapping = new AtomicBoolean(false);
		startGame = new AtomicBoolean(false);
		numberOfPlayers = new AtomicInteger(10);
		initializePoints((short) numberOfPlayers.get());
	}
	
	
	public void initializePoints(final short nPlayers) {
		this.numberOfPlayers.set(nPlayers);
		totalPoints = new AtomicInteger();
	}
	
	
	public void setColor(final int id, final int color) {
		synchronized(idAndColors) {
			Log.d("SOON", "ADDED NEW COLOR: " + id +".  " + color);
			idAndColors.put(id, color);
		}
		
	}
	
	
	public void addScore(final ShortPair score) {
		synchronized(finalScores) {
			Log.d("SOON", "ADDED SCORE: " + score.id + ", " + score.second);
			finalScores.add(score);
		}
	}
	
	
	public int getScore(final short id) {
		synchronized(finalScores) {
			for(ShortPair score : finalScores) {
				if(score.id == id) {
					return score.second;
				}
			}
		}
		return -1;
	}
	
	
	public void clearScores() {
		synchronized( finalScores ){
			finalScores.clear();
		}
	}
	
	
	public ConcurrentHashMap<Integer, Integer> getIdAndColors() {
		synchronized(idAndColors) {
			return new ConcurrentHashMap<Integer, Integer>(idAndColors);
		}
	}
	
	
	public int getColor(final int id) {
		synchronized(idAndColors) {
			return idAndColors.get(id);
		}
	}
	
	
	public void addServer(final Server server) {
		synchronized(servers) {
			for(Server serverInList : servers) {
				if(serverInList.ip.equals(server.ip)) {
					serverInList.slotsTaken = server.slotsTaken;
					return;
				}
			}
			Log.d("BROAD", "ADDED SERVER: " + server.name + ", " + server.ip + ", " + server.port);
			servers.add(server);
		}
	}
	
	
	ArrayList<Server> getServers() {
		synchronized(servers) {
			return new ArrayList<Server>(servers);
		}
	}
	
	
	public void clearServers() {
		synchronized(servers) {
			servers.clear();
		}
	}
	
	
	public void setServer(final Server server) {
		synchronized(server) {
			Log.d("BROWSE", "Set server: " + server.name + ",  " + server.ip);
			this.server = server;
		}
	}
	

	public Server getServer() {
		synchronized(server) {
			return server;
		}
	}
	
	
	// =========================================
	// Get methods
	// =========================================
	
	public boolean isRunning() {
		return isRunning.get();
	}
	
	
	public boolean shouldStartGame() {
		return startGame.get();
	}
	
	
	public boolean isMapped() {
		return mapDone.get();
	}
	
	
	public boolean isRemapping() {
		return isRemapping.get();
	}
	
	
	public int getNumberOfPlayers() {
		return numberOfPlayers.get();
	}
	
	
	public ConcurrentHashMap<Integer, Integer> getPoints() {
		return idAndPoints;
	}
	
	
	public AtomicInteger getTotalPoints() {
		return totalPoints;
	}
	
	
	public boolean pointsIsUpdated() {
		return updatedPoints.get();
	}
	
	
	public float getDeviceAngle() {
		synchronized( this.deviceAngleMutex ) {
			return this.deviceAngle;
		}
	}
	
	
	public float getDeviceMiddleX() {
		synchronized( this.deviceMiddleXMutex ) {
			return this.deviceMiddleX;
		}
	}
	
	
	public float getDeviceMiddleY() {
		synchronized( this.deviceMiddleYMutex ) {
			return this.deviceMiddleY;
		}
	}
	
	
	public GLOBAL_STATE__ getInternalState() {
		synchronized( this.internalStateMutex ) {
			return this.internalState;
		}
	}
	
	
	public float getRecivieDelay() {
		synchronized( this.reciveDelayMutex ) {
			return this.reciveDelay;
		}
	}
	
	
	public float getSendDelay() {
		synchronized( this.sendDelayMutex ) {
			return this.sendDelay;
		}
	}
	
	public int getNClocks() {
		return this.nClocks.get();
	}
	

	public float getMainMiddleX() {
		synchronized( this.mainMiddleXMutex ) {
			return this.mainMiddleX;
		}
	}


	public float getMainMiddleY() {
		synchronized( this.mainMiddleYMutex ) {
			return this.mainMiddleY;
		}
	}
	
	
	public float getMiddleAngle() {
		synchronized( this.middleAngleMutex ) {
			return this.middleAngle;
		}
	}
	
	
	public int getGameTimeInSeconds() {
		return gameTimeInSeconds.get();
	}
	
	
	public int getDeviceId() {
		return deviceId.get();
	}
	
	
	public int getMyPoints() {
		return idAndPoints.get(deviceId.get());
	}
	
	
	// =========================================
	// Set methods
	// =========================================
	
	public void setMapDone(final boolean mapDone) {
		this.mapDone.set(mapDone);
	}
	
	
	public void setIsRemapping(final boolean isRemapping) {
		this.isRemapping.set(isRemapping);
	}
	
	
	public void setIsRunning(final boolean isRunning) {
		this.isRunning.set(isRunning);
	}
	
	
	public void setStartGame(final boolean startGame) {
		this.startGame.set(startGame);
	}
	
	
	public void setPoints(final int id, final int points) {
		idAndPoints.put(id, points);
	}


	public void setTotalPoints(final int totalPoints) {
		this.totalPoints.set(totalPoints);
	}
	
	
	public void setPointsIsUpdated(final boolean isUpdated) {
		this.updatedPoints.set(isUpdated);
	}
	
	
	public void setDeviceAngle(float newDeviceAngle) {
		synchronized( this.deviceAngleMutex ) {
			this.deviceAngle = newDeviceAngle;
		}
	}
	
	
	public void setDeviceMiddleX(float newDeviceMiddleX) {
		synchronized( this.deviceMiddleXMutex ) {
			this.deviceMiddleX = newDeviceMiddleX;
		}
	}
	
	
	public void setDeviceMiddleY(float newDeviceMiddleY) {
		synchronized( this.deviceMiddleYMutex ) {
			this.deviceMiddleY = newDeviceMiddleY;
		}
	}
	
	
	public void setMainMiddleX(float newMainMiddleX) {
		synchronized( this.mainMiddleXMutex ) {
			this.mainMiddleX = newMainMiddleX;
		}
	}
	
	
	public void setMainMiddleY(float newMainMiddleY) {
		synchronized( this.mainMiddleYMutex ) {
			this.mainMiddleY = newMainMiddleY;
		}
	}
	
	
	public void setInternalState(GLOBAL_STATE__ newInternalState) {
		synchronized( this.internalStateMutex ) {
			this.internalState = newInternalState;
		}
	}
	
	
	public void setRecivieDelay(float newReciveDelay) {
		synchronized( this.reciveDelayMutex ){
			this.reciveDelay = newReciveDelay;
		}
	}
	
	
	public void setSendDelay(float newSendDelay) {
		synchronized( this.sendDelayMutex ){
			this.sendDelay = newSendDelay;
		}
	}
	
	
	public void setNClocks(int newNClocks) {
		this.nClocks.set(newNClocks);
	}
	
	
	public void incrementNClocks() {
		this.nClocks.incrementAndGet();
	}


	public void setMiddleAngle(final float middleAngle) {
		synchronized( this.middleAngleMutex ){
			this.middleAngle = middleAngle;
		}
	}


	public void setGameTimeInSeconds(final short gameTimeInSeconds) {
		this.gameTimeInSeconds.set(gameTimeInSeconds);
	}


	public void setDeviceId(final short deviceId) {
		this.deviceId.set(deviceId);
	}


}
