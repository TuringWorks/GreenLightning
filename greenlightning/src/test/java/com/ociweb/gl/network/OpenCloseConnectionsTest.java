package com.ociweb.gl.network;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.ociweb.gl.api.GreenRuntime;
import com.ociweb.gl.test.ParallelClientLoadTester;
import com.ociweb.gl.test.ParallelClientLoadTesterConfig;
import com.ociweb.pronghorn.network.ClientSocketReaderStage;

public class OpenCloseConnectionsTest {

	@Test
	public void testConnectionRemainsOpen() {

		
		//ClientSocketWriterStage.showWrites = true; 
		//ServerSocketReaderStage.showRequests = true; 
		//ServerSocketWriterStage.showWrites = true;
		//ClientSocketReaderStage.showResponse = true; 

		boolean telemetry = false;
		StringBuilder target = new StringBuilder();
		
		GreenRuntime.run(new OpenCloseTestServer(8078, telemetry, target));		
		
		StringBuilder results = new StringBuilder();
		int cyclesPerTrack = 1000;
		ParallelClientLoadTesterConfig config = new ParallelClientLoadTesterConfig(
				1, cyclesPerTrack, 8078, "neverclose", telemetry, results);
		
		GreenRuntime.testConcurrentUntilShutdownRequested(
				new ParallelClientLoadTester(config, null),
			   20* 10_000);
		
		String captured = results.toString();
		
		assertTrue(captured, captured.contains("Total messages: 1000"));
		assertTrue(captured, captured.contains("Send failures: 0 out of 1000"));
		assertTrue(captured, captured.contains("Timeouts: 0"));
				
		//System.out.println(captured);
		
	}

	//TODO: needs more work
	@Ignore
	public void testConnectionCloses() {
			
		ClientSocketReaderStage.abandonSlowConnections = false;
		
		//HTTP1xRouterStage.showHeader = true;
		//ServerSocketReaderStage.showRequests = true;
		//ClientSocketReaderStage.showResponse = true;
		//ClientSocketWriterStage.showWrites = true;
		//ServerSocketWriterStage.showWrites = true;
		
		StringBuilder target = new StringBuilder();
		
		GreenRuntime.run(new OpenCloseTestServer(8089, false, target));
						
		StringBuilder results = new StringBuilder();
		int cyclesPerTrack = 100;
		ParallelClientLoadTesterConfig config = new ParallelClientLoadTesterConfig(
				1, cyclesPerTrack, 8089, "alwaysclose", false, results);
		
		GreenRuntime.testConcurrentUntilShutdownRequested(
				new ParallelClientLoadTester(config, null),
				10_000);
		
		String captured = results.toString();
		
		System.out.println(captured);
		
		assertTrue(captured, captured.contains("Total messages: "+cyclesPerTrack));
		assertTrue(captured, captured.contains("Send failures: 0 out of "+cyclesPerTrack));
		assertTrue(captured, captured.contains("Timeouts: 0"));
		
		
				
	}
	
	//add test for a server where the clients keep connecting and the old one must be dropped.
	//we only have 5 connections in the server now..
	
	@Test
	public void testConnectionOverload() {
			
		//HTTP1xResponseParserStage.showData = true;
		//ServerSocketReaderStage.showRequests = true;
		//ClientSocketReaderStage.showResponse = true;
		//ClientSocketWriterStage.showWrites = true;
		//ServerSocketWriterStage.showWrites = true;
		
		StringBuilder target = new StringBuilder();
		
		boolean telemetry = false;
		GreenRuntime.run(new OpenCloseTestServer(8091, telemetry, target));
		
		StringBuilder results = new StringBuilder();
		//we only have 8 connections so this test will run 16
		int parallelTracks = 8;//10;
		ParallelClientLoadTesterConfig config = new ParallelClientLoadTesterConfig(
				parallelTracks, 20, 8091, "neverclose", telemetry, results);
		
		//after 4 seconds it should be able to get in.
		//TODO: test client must try again when dropped without notice by the server...

		GreenRuntime.testConcurrentUntilShutdownRequested(
				new ParallelClientLoadTester(config, null),
			10000*	20_000);
		
		
		String captured = results.toString();
		
		//assertTrue(captured, captured.contains("Total messages: 100"));
		assertTrue(captured, captured.contains("Send failures: 0 out of"));
		assertTrue(captured, captured.contains("Timeouts: 0"));

		
	}
	
		
	
}
