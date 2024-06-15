package com.example.audio_mp3_player;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

public class MP3Player extends JFrame {
    private static final long serialVersionUID = 1L;
    private List<File> mp3Files;
    private JButton playPauseButton;
    private JButton openButton;
    private JButton nextButton;
    private JButton previousButton;
    private JList<String> songList;
    private boolean isPlaying = false;
    private int currentIndex = -1;
    private MediaPlayer mediaPlayer;
    private JProgressBar progressBar;
    private Duration pausedTime;

    public MP3Player() {
        super("MP3 Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        Icon playIcon = new ImageIcon("play-button.png");
        Icon pauseIcon = new ImageIcon("pause.png");

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100); // Assuming 100 represents the duration of the song
        progressBar.setValue(0); // Initial value

        playPauseButton = new JButton("Play");
        playPauseButton.setBackground(new Color(156, 147, 203));
        playPauseButton.setForeground(Color.WHITE);
        playPauseButton.setBorderPainted(false);
        playPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePlayPause();
            }
        });

        openButton = new JButton("Open");
        openButton.setBackground(new Color(156, 147, 203));
        openButton.setForeground(Color.WHITE);
        openButton.setBorderPainted(false);
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFileChooser();
            }
        });

        nextButton = new JButton("Next");
        nextButton.setBackground(new Color(156, 147, 203));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBorderPainted(false);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playNextSong();
            }
        });

        previousButton = new JButton("Prev");
        previousButton.setBackground(new Color(156, 147, 203));
        previousButton.setForeground(Color.WHITE);
        previousButton.setBorderPainted(false);
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playPreviousSong();
            }
        });

        songList = new JList<>();
        songList.setBackground(new Color(75, 65, 128));
        songList.setCellRenderer(new CustomListCellRenderer());
        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    playSelectedSong();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(songList);

        // Create panels
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Add components to panels
        topPanel.add(openButton);
        topPanel.setBackground(new Color(156, 147, 203));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.setBackground(new Color(156, 147, 203));
        bottomPanel.add(previousButton);
        bottomPanel.add(playPauseButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(progressBar);
        bottomPanel.setBackground(new Color(156, 147, 203));

        // Add panels to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(75, 65, 128));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        // Initialize JavaFX toolkit
        initJavaFX();
    }

    private void initJavaFX() {
        // Initialize JavaFX toolkit
        new JFXPanel();
        Platform.runLater(() -> {
            // JavaFX code here
            mediaPlayer = new MediaPlayer(null);
        });
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (mediaPlayer != null) {
                double progress = mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                int progressPercentage = (int) (progress * 100);
                progressBar.setValue(progressPercentage);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void togglePlayPause() {
        if (isPlaying) {
            pausePlayback();
            playPauseButton.setText("Play");
        } else {
            if (pausedTime != null) {
                resumePlayback(); // Resume from the stored position
            } else {
                startPlayback();
            }
            playPauseButton.setText("Pause");
        }
        isPlaying = !isPlaying;
    }

    private void startPlayback() {
        int selectedIndex = songList.getSelectedIndex();
        if (selectedIndex != -1) {
            currentIndex = selectedIndex;
            File selectedFile = mp3Files.get(selectedIndex);
            Media media = null;
            if (selectedFile != null && selectedFile.exists()) {
                media = new Media(selectedFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
            } else {
                System.out.println("Selected file is null or does not exist.");
            }
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } else {
            System.out.println("No file selected.");
        }
    }

    private void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            pausedTime = mediaPlayer.getCurrentTime(); // Store the current playback position
        }
    }

    private void resumePlayback() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.seek(pausedTime); // Resume playback from the stored position
            mediaPlayer.play();
        }
    }

    private void loadMP3FilesFromDirectory(File directory) {
        mp3Files = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                    mp3Files.add(file);
                }
            }
        }
        List<String> songNames = new ArrayList<>();
        for (File mp3File : mp3Files) {
            songNames.add(mp3File.getName());
        }
        songList.setListData(songNames.toArray(new String[0]));
    }

    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            loadMP3FilesFromDirectory(selectedDirectory);
        }
    }

    private void playSelectedSong() {
        if (!isPlaying) {
            startPlayback();
            playPauseButton.setText("Pause");
            isPlaying = true;
        }
    }

    private void playNextSong() {
        if (currentIndex != -1 && currentIndex < mp3Files.size() - 1) {
            pausePlayback(); // Pause the current song
            currentIndex++;
            songList.setSelectedIndex(currentIndex);
            startPlayback();
            playPauseButton.setText("Pause");
            isPlaying = true;
        }
    }

    private void playPreviousSong() {
        if (currentIndex > 0) {
            pausePlayback(); // Pause the current song
            currentIndex--;
            songList.setSelectedIndex(currentIndex);
            startPlayback();
            playPauseButton.setText("Pause");
            isPlaying = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MP3Player mp3Player = new MP3Player();
                mp3Player.setVisible(true);
            }
        });
    }
}
