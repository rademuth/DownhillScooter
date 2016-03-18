package edu.virginia.engine.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {

	private static SoundManager instance = null;
	
	private Map<String,Clip> soundEffects;
	private Map<String,Clip> songs;
	
	private SoundManager() {
		this.soundEffects = new HashMap<String,Clip>();
		this.songs = new HashMap<String,Clip>();
	}
	
	public static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}
	
	public Clip getClipFromFile(String filename) {
		String filepath = ("resources" + File.separator + filename);
		Clip clip = null;
		try {
            File file = new File(filepath);
            if (file.exists()) {
                AudioInputStream sound = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(sound);
            }
            else {
                throw new RuntimeException("Sound: file not found: " + filename);
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Malformed URL: " + e);
        }
        catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Unsupported Audio File: " + e);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Input/Output Error: " + e);
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
        }
		return clip;
	}
	
	public void loadSoundEffect(String id, String filename) {
		Clip clip = getClipFromFile(filename);
		soundEffects.put(id,clip);
	}
	
	public void playSoundEffect(String id) {
		Clip clip = soundEffects.get(id);
		if (clip != null) {
			clip.setFramePosition(0);
			clip.start();
		}
	}
	
	public void loadMusic(String id, String filename) {
		Clip clip = getClipFromFile(filename);
		songs.put(id,clip);
	}
	
	public void playMusic(String id) {
		Clip clip = songs.get(id);
		if (clip != null) {
			clip.setFramePosition(0);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
		
}
