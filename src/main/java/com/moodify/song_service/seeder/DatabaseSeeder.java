// package com.moodify.song_service.seeder;

// import com.moodify.song_service.models.Song;
// import com.moodify.song_service.repositories.SongRepository;

// import jakarta.annotation.PostConstruct;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import java.util.List;

// @Component
// public class DatabaseSeeder {

//     private final SongRepository songRepository;

//     @Autowired
//     public DatabaseSeeder(SongRepository songRepository) {
//         this.songRepository = songRepository;
//     }

//     @PostConstruct
//     public void seedDatabase() {
//         List<Song> songs = List.of(
//             new Song("1hA697u7e1jX2XM8sWA6Uy", "Apna Bana Le", List.of("Sachin-Jigar", "Arijit Singh", "Amitabh Bhattacharya")),
//             new Song("0BiDnJFB3TlRB6aji7jUz3", "Rey Sin Reina", List.of("Julión Álvarez y su Norteño Banda")),
//             new Song("2X485T9Z5Ly0xyaghN73ed", "Let It Happen", List.of("Tame Impala")),
//             new Song("5FXMRdJjKq1BIX4e8Eg9mK", "Ajab Si", List.of("Vishal-Shekhar", "KK")),
//             new Song("5Bp7TFENcgsdLCL8fMOSKs", "Adora a bunda, odeia o rosto (feat. Veigh & Niink)", List.of("Supernova Ent", "G.A", "Ghard", "Veigh", "Niink")),
//             new Song("1QV6tiMFM6fSOKOGLMHYYg", "Poker Face", List.of("Lady Gaga")),
//             new Song("5yfhzEct1ulyU6g5oVRraj", "KNOW ABOUT ME", List.of("NMIXX")),
//             new Song("5p1eiAHN5xYAMrSUDKxidI", "Te Amo", List.of("Calema")),
//             new Song("6ECp64rv50XVz93WvxXMGF", "This Love", List.of("Maroon 5")),
//             new Song("7kb6IV0z8Nd7vejSdAq0YN", "Stop The Rain (TABLO X RM)", List.of("TABLO", "RM")),
//             new Song("3yrSvpt2l1xhsV9Em88Pul", "Brown Eyed Girl", List.of("Van Morrison")),
//             new Song("1k2pQc5i348DCHwbn5KTdc", "Pink Pony Club", List.of("Chappell Roan")),
//             new Song("5MKbWaXeSQSmzmo7gPQha1", "Amor de Vago", List.of("La T y La M", "Malandro")),
//             new Song("2qztPHA0xuc4pPRYJFDTIP", "Avantura", List.of("Rasta")),
//             new Song("0fKK51bU6lcCCwdNnv64t3", "DEPORTIVO", List.of("Blessd", "Anuel AA", "Ovy On The Drums")),
//             new Song("1TE8AAk9koG0UJco6ZAUfx", "Rock and A Hard Place", List.of("Bailey Zimmerman")),
//             new Song("4cluDES4hQEUhmXj6TXkSo", "What Makes You Beautiful", List.of("One Direction")),
//             new Song("4MF0VMffyxHsy3GZwwUnAs", "A Morte do Autotune", List.of("Matuê")),
//             new Song("09mEdoA6zrmBPgTEN5qXmN", "Call Out My Name", List.of("The Weeknd")),
//             new Song("6TfBA04WJ3X1d1wXhaCFVT", "You're Gonna Go Far, Kid", List.of("The Offspring")),
//             new Song("2GSK9VfsKWpVOV6ZzKiMA4", "alone - Remix", List.of("FOLA", "BNXN")),
//             new Song("3Fcfwhm8oRrBvBZ8KGhtea", "Viva La Vida", List.of("Coldplay")),
//             new Song("3HrHhTWonBqF4mDasi8xvC", "BACKBONE", List.of("Chase & Status", "Stormzy")),
//             new Song("1dzQoRqT5ucxXVaAhTcT0J", "Just Dance", List.of("Lady Gaga", "Colby O'Donis"))
//         );

//         for (Song song : songs) {
//             if (!songRepository.existsBySpotifyId(song.getSpotifyId())) {
//                 songRepository.save(song);
//                 System.out.println("Seeded song: " + song.getName());
//             }
//         }
//     }
// }
