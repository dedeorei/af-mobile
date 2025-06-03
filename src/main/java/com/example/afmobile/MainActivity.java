package com.example.afmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    TextView questionText;
    Button optionA, optionB, optionC, optionD;

    ImageView bandImage;

    TextView otherBandsLabel;
    LinearLayout otherBandsContainer;
    ImageView band1Image, band2Image, band3Image;

    int currentQuestion = 0;

    // Contadores para cada banda
    int scoreA = 0; // Deftones
    int scoreB = 0; // Slipknot
    int scoreC = 0; // Iron Maiden
    int scoreD = 0; // Alice In Chains

    String[] questions = {
            "Como você descreveria seu estilo em uma noite de diversão?",
            "Qual dessas frases mais combina com sua visão de vida?",
            "Qual desses ambientes musicais você prefere?",
            "Se você fosse uma cor, qual delas seria?",
            "Qual das seguintes atividades combina mais com você?"
    };

    String[][] options = {
            {"Um clima mais atmosférico e introspectivo, curtindo algo diferente e experimental.",
                    "Energia intensa, muita adrenalina e um jeito mais agressivo de se divertir.",
                    "Clássico e animado, com um toque de nostalgia e muita história para contar.",
                    "Algo mais sombrio e melancólico, mas que ainda toca o coração profundamente."},

            {"“Explorar emoções profundas e não ter medo do desconhecido.”",
                    "“Libertar-se das amarras e enfrentar o caos com força.”",
                    "“Valorizar a tradição, a coragem e a perseverança.”",
                    "“Encarar as dificuldades e transformá-las em arte.”"},

            {"Sons etéreos, camadas sonoras densas e texturas ricas.",
                    "Ritmos pesados, batidas rápidas e vocais agressivos.",
                    "Melodias épicas, solos de guitarra e letras cheias de histórias.",
                    "Riffs sombrios, vocais melancólicos e uma vibe introspectiva."},

            {"Tons de roxo e cinza, misteriosos e sofisticados.",
                    "Vermelho intenso, cheio de energia e paixão.",
                    "Azul metálico, clássico e forte.",
                    "Preto e verde escuro, melancólico e profundo."},

            {"Meditar ou criar algo artístico em um ambiente calmo.",
                    "Praticar esportes radicais ou qualquer coisa que aumente a adrenalina.",
                    "Participar de eventos culturais e celebrar tradições.",
                    "Escrever ou refletir sobre suas emoções e desafios pessoais."}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        questionText = findViewById(R.id.questionText);
        bandImage = findViewById(R.id.bandImage);
        otherBandsLabel = findViewById(R.id.otherBandsLabel);
        otherBandsContainer = findViewById(R.id.otherBandsContainer);
        band1Image = findViewById(R.id.band1Image);
        band2Image = findViewById(R.id.band2Image);
        band3Image = findViewById(R.id.band3Image);
        optionA = findViewById(R.id.buttonA);
        optionB = findViewById(R.id.buttonB);
        optionC = findViewById(R.id.buttonC);
        optionD = findViewById(R.id.buttonD);


        setQuestion();

        optionA.setOnClickListener(v -> {
            scoreA++;
            nextQuestion();
        });

        optionB.setOnClickListener(v -> {
            scoreB++;
            nextQuestion();
        });

        optionC.setOnClickListener(v -> {
            scoreC++;
            nextQuestion();
        });

        optionD.setOnClickListener(v -> {
            scoreD++;
            nextQuestion();
        });
    }

    private void setQuestion() {
        if (currentQuestion < questions.length) {
            questionText.setText(questions[currentQuestion]);
            optionA.setText(options[currentQuestion][0]);
            optionB.setText(options[currentQuestion][1]);
            optionC.setText(options[currentQuestion][2]);
            optionD.setText(options[currentQuestion][3]);
        }
    }

    private void nextQuestion() {
        currentQuestion++;
        if (currentQuestion < questions.length) {
            setQuestion();
        } else {
            showResult();
        }
    }

    private void showResult() {
        String result;
        String recommendedBand = "";
        String[] allBands = {"Deftones", "Slipknot", "Iron Maiden", "Alice In Chains"};
        int[] allImages = {
                R.drawable.deftones,
                R.drawable.slipknot,
                R.drawable.iron_maiden,
                R.drawable.alice_in_chains
        };

        int recommendedIndex = -1;
        int maxScore = Math.max(Math.max(scoreA, scoreB), Math.max(scoreC, scoreD));

        if (maxScore == scoreA) {
            recommendedBand = "Deftones";
            recommendedIndex = 0;
            bandImage.setImageResource(R.drawable.deftones);
        } else if (maxScore == scoreB) {
            recommendedBand = "Slipknot";
            recommendedIndex = 1;
            bandImage.setImageResource(R.drawable.slipknot);
        } else if (maxScore == scoreC) {
            recommendedBand = "Iron Maiden";
            recommendedIndex = 2;
            bandImage.setImageResource(R.drawable.iron_maiden);
        } else {
            recommendedBand = "Alice In Chains";
            recommendedIndex = 3;
            bandImage.setImageResource(R.drawable.alice_in_chains);
        }

        // Mostra o resultado principal
        questionText.setText("Recomendamos ouvir: " + recommendedBand);
        bandImage.setVisibility(View.VISIBLE);

// Mostra as outras bandas
        otherBandsLabel.setVisibility(View.VISIBLE);
        otherBandsContainer.setVisibility(View.VISIBLE);

// Exibe as outras 3 bandas que não foram recomendadas
        int count = 0;
        for (int i = 0; i < allBands.length; i++) {
            if (i == recommendedIndex) continue;

            switch (count) {
                case 0:
                    band1Image.setImageResource(allImages[i]);
                    break;
                case 1:
                    band2Image.setImageResource(allImages[i]);
                    break;
                case 2:
                    band3Image.setImageResource(allImages[i]);
                    break;
            }
            count++;
        }

// Esconde os botões
        optionA.setVisibility(View.GONE);
        optionB.setVisibility(View.GONE);
        optionC.setVisibility(View.GONE);
        optionD.setVisibility(View.GONE);

// Salva no Firestore
        saveResultToFirestore(recommendedBand);

        }


    private void saveResultToFirestore(String recommendedBand) {
        // Cria um mapa com os dados que quer salvar
        Map<String, Object> result = new HashMap<>();
        result.put("band_recommended", recommendedBand);
        result.put("timestamp", System.currentTimeMillis());

        // Salva na coleção "quiz_results"
        db.collection("quiz_results")
                .add(result)
                .addOnSuccessListener(documentReference -> {
                    // Sucesso ao salvar
                    // Pode mostrar um Toast ou log
                    // Toast.makeText(this, "Resultado salvo com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar
                    // Toast.makeText(this, "Erro ao salvar resultado.", Toast.LENGTH_SHORT).show();
                });
    }

}