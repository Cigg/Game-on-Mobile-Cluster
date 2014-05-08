package com.pussycat.minions;

import java.io.File;

import com.pussycat.framework.Audio;
import com.pussycat.framework.Music;

public class MusicWidget {
	
	private Music music;
	
	public MusicWidget(Audio audio) {
		music = audio.createMusic("sounds" + File.separator + "hela.wav");
		music.setLooping(true);
	}
	
	public void play() {
		music.play();
	}
	
	public void stop() {
		music.stop();
	}

}
