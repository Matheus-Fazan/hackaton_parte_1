# Guia de conteúdos Android — projetos-mobile-android

> Levantamento dos 16 repositórios públicos da organização em 08/09/2026. Os exemplos abaixo foram sintetizados a partir dos projetos para ficarem seguros, legíveis e reutilizáveis; não são cópias literais. Todos usam **Java + Views/XML**, como os repositórios analisados.

## Mapa de estudo

| Repositório | Conteúdo principal |
|---|---|
| [WebView](https://github.com/projetos-mobile-android/WebView) | Navegação web embarcada |
| [Fragment](https://github.com/projetos-mobile-android/Fragment) | Fragments, Navigation Component e Bundle |
| [SplashScreen](https://github.com/projetos-mobile-android/SplashScreen) | Tela de abertura e transição de `Activity` |
| [RecycleView](https://github.com/projetos-mobile-android/RecycleView) | `RecyclerView`, Adapter, ViewHolder e Glide |
| [Produto](https://github.com/projetos-mobile-android/Produto) | Retrofit, API REST e lista de produtos |
| [Playlist](https://github.com/projetos-mobile-android/Playlist) | Firestore em tempo real e votação |
| [PedraPapelTesoura](https://github.com/projetos-mobile-android/PedraPapelTesoura) | Eventos, `Random` e regras de jogo |
| [ChatBot](https://github.com/projetos-mobile-android/ChatBot) | Firebase AI / Gemini assíncrono |
| [FirebaseAuth](https://github.com/projetos-mobile-android/FirebaseAuth) | Firebase Authentication e Google Sign-In |
| [Tradutor](https://github.com/projetos-mobile-android/Tradutor) | Sem arquivos no snapshot analisado |
| [IntentBundle](https://github.com/projetos-mobile-android/IntentBundle) | Intents explícitas/implícitas, extras e diálogos |
| [GridPedido](https://github.com/projetos-mobile-android/GridPedido) | Carrinho, callbacks, `RecyclerView` e pagamento |
| [MVVM](https://github.com/projetos-mobile-android/MVVM) | MVVM, `ViewModel` e regra de negócio |
| [SharedPreferences](https://github.com/projetos-mobile-android/SharedPreferences) | Cache local JSON e modo offline |
| [MenuButton](https://github.com/projetos-mobile-android/MenuButton) | Bottom navigation, toolbar e mapas |
| [MySelf](https://github.com/projetos-mobile-android/MySelf) | Câmera, galeria, `FileProvider`, MediaStore e upload |

## 1. WebView

O projeto abre uma página dentro do app, mostra progresso durante o carregamento e trata o botão Voltar. É útil para conteúdo web que não exige toda a experiência de um navegador.

```java
WebView webView = findViewById(R.id.web);
ProgressBar progress = findViewById(R.id.progressBar);

WebSettings settings = webView.getSettings();
settings.setJavaScriptEnabled(true); // somente se a página realmente precisar

webView.setWebViewClient(new WebViewClient() {
    @Override public void onPageStarted(WebView view, String url, Bitmap icon) {
        progress.setVisibility(View.VISIBLE);
    }

    @Override public void onPageFinished(WebView view, String url) {
        progress.setVisibility(View.GONE);
    }
});
webView.loadUrl("https://exemplo.com");
```

Também declare `android.permission.INTERNET` no `AndroidManifest.xml`. Em produção, limite os domínios aceitos em `shouldOverrideUrlLoading`; habilitar JavaScript para páginas não confiáveis aumenta a superfície de risco.

## 2. Fragment + Navigation Component

O repositório usa dois fragments e envia texto entre eles com `Bundle`. A vantagem é trocar partes da mesma `Activity` sem recriar a tela inteira.

```java
Bundle args = new Bundle();
args.putString("texto", editTexto.getText().toString());
Navigation.findNavController(view).navigate(R.id.acao_a_para_b, args);

// No fragment de destino
String texto = requireArguments().getString("texto", "");
TextView saida = view.findViewById(R.id.saida);
saida.setText(texto);
```

Para a barra superior, o projeto associa `NavController` e `Toolbar` com `NavigationUI.setupWithNavController(toolbar, navController)`. Em projetos novos, prefira Safe Args para evitar chaves de `Bundle` digitadas manualmente.

## 3. Splash screen

Os projetos `SplashScreen`, `PedraPapelTesoura`, `Produto` e `GridPedido` usam uma tela inicial antes da principal. Uma implementação atual evita bloquear a UI e fecha a tela de abertura da pilha:

```java
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    startActivity(new Intent(SplashActivity.this, MainActivity.class));
    finish();
}, 1500);
```

O repositório também usa Glide para carregar um GIF em `ImageView`. Para Android 12+, vale preferir a SplashScreen API (`installSplashScreen()`), que integra melhor com o sistema.

## 4. RecyclerView, Adapter, ViewHolder e Glide

`RecycleView` cria uma lista de pessoas, infla um card e usa Glide para imagens remotas. Esse é o padrão de lista eficiente do Android: a tela reutiliza os itens visíveis.

```java
public final class PessoaAdapter
        extends RecyclerView.Adapter<PessoaAdapter.Holder> {
    private final List<Pessoa> pessoas;

    public PessoaAdapter(List<Pessoa> pessoas) { this.pessoas = pessoas; }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pessoa, parent, false);
        return new Holder(item);
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        Pessoa pessoa = pessoas.get(position);
        h.nome.setText(pessoa.getNome());
        Glide.with(h.itemView).load(pessoa.getFotoUrl()).circleCrop().into(h.foto);
    }

    @Override public int getItemCount() { return pessoas.size(); }
}

recyclerView.setLayoutManager(new LinearLayoutManager(this));
recyclerView.setAdapter(new PessoaAdapter(lista));
```

O mesmo mecanismo aparece em `Playlist`, `Produto`, `SharedPreferences` e `GridPedido`; muda apenas o modelo e a ação de cada item. Para listas que mudam bastante, `ListAdapter` + `DiffUtil` é a evolução recomendada.

## 5. Retrofit e consumo de API REST

`Produto`, `GridPedido` e `SharedPreferences` consomem APIs e transformam JSON em objetos Java com Retrofit + Gson. Defina a interface HTTP separada da tela:

```java
public interface ProdutoApi {
    @GET("products")
    Call<List<ProdutoJson>> listar();
}

Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/") // a barra final é obrigatória
        .addConverterFactory(GsonConverterFactory.create())
        .build();

retrofit.create(ProdutoApi.class).listar().enqueue(new Callback<List<ProdutoJson>>() {
    @Override public void onResponse(Call<List<ProdutoJson>> call,
                                     Response<List<ProdutoJson>> response) {
        if (!response.isSuccessful() || response.body() == null) return;
        adapter.atualizar(response.body());
    }
    @Override public void onFailure(Call<List<ProdutoJson>> call, Throwable error) {
        Toast.makeText(MainActivity.this, "Falha de conexão", Toast.LENGTH_SHORT).show();
    }
});
```

Valide `response.isSuccessful()` e `body()` antes de usar a resposta. O projeto `Produto` está incompleto no snapshot; use-o como referência de estrutura, não como implementação pronta para compilar.

## 6. Carrinho, total e callback entre Adapter e tela

`GridPedido` e `SharedPreferences` atualizam a quantidade de itens no Adapter e refletem o total na `Activity`. Em vez de o Adapter conhecer a tela, entregue uma função de retorno:

```java
public interface AoAlterarCarrinho {
    void onTotalAlterado(double total);
}

// dentro do clique no botão + do Adapter
produto.setQuantidade(produto.getQuantidade() + 1);
total += produto.getPreco();
listener.onTotalAlterado(total);
notifyItemChanged(getBindingAdapterPosition());

// na Activity
adapter = new ProdutoAdapter(produtos, total ->
        textoTotal.setText(String.format(Locale.US, "Total: R$ %.2f", total)));
```

O `GridPedido` também passa o total para a tela de pagamento com `Intent.putExtra`. Para dinheiro real, use `BigDecimal` ou valores inteiros em centavos, nunca `double`.

## 7. Firebase Firestore em tempo real

`Playlist` persiste músicas numa coleção Firestore, ordena por votos e atualiza a lista ao receber mudanças. Um listener em tempo real torna a interface colaborativa sem polling:

```java
CollectionReference playlist = FirebaseFirestore.getInstance().collection("playlist");
ListenerRegistration registro = playlist.orderBy("votos", Query.Direction.DESCENDING)
        .addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) return;
            adapter.atualizar(snapshot.toObjects(Musica.class));
        });

playlist.document(musica.getId()).update("votos", FieldValue.increment(1));

@Override protected void onStop() {
    super.onStop();
    if (registro != null) registro.remove();
}
```

O projeto ainda salva o nome localmente e permite excluir apenas a música indicada pelo próprio usuário. Essa regra também precisa existir nas **Firestore Security Rules**; validar somente no cliente não protege os dados.

## 8. Pedra, papel e tesoura

O projeto é um exercício de listeners, estado da jogada, aleatoriedade e condicionais. Gere as três opções com `nextInt(3)`, não `nextInt(4)`, para não criar um caso vazio:

```java
String[] opcoes = {"pedra", "papel", "tesoura"};
String app = opcoes[new Random().nextInt(opcoes.length)];

boolean venceu = (usuario.equals("pedra") && app.equals("tesoura"))
        || (usuario.equals("papel") && app.equals("pedra"))
        || (usuario.equals("tesoura") && app.equals("papel"));

resultado.setText(usuario.equals(app) ? "Empate" : venceu ? "Você venceu" : "Você perdeu");
```

Antes de jogar, valide que o usuário fez uma escolha. Isso impede resultado incoerente quando nenhum ícone foi tocado.

## 9. Chatbot com Firebase AI / Gemini

`ChatBot` cria um `GenerativeModel`, envia o texto do usuário e recebe `GenerateContentResponse` de forma assíncrona. O cuidado essencial é atualizar a interface na thread principal:

```java
GenerativeModel model = FirebaseAI.getInstance(GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash-lite");
GenerativeModelFutures futures = GenerativeModelFutures.from(model);

Content prompt = new Content.Builder().addText(pergunta).build();
Futures.addCallback(futures.generateContent(prompt), new FutureCallback<>() {
    @Override public void onSuccess(GenerateContentResponse resposta) {
        runOnUiThread(() -> saida.append("\nIA: " + resposta.getText()));
    }
    @Override public void onFailure(Throwable erro) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                "Não foi possível responder", Toast.LENGTH_SHORT).show());
    }
}, Executors.newSingleThreadExecutor());
```

O repositório já apresenta o fluxo principal. Evite expor chaves no APK, trate resposta vazia e, para conversas longas, mantenha histórico com limite de tamanho.

## 10. Firebase Authentication

Após a atualização, `FirebaseAuth` reúne cadastro com e-mail/senha, login, atualização de perfil, redefinição de senha e Google Sign-In. Exemplo de cadastro com validações mínimas:

```java
FirebaseAuth auth = FirebaseAuth.getInstance();
auth.createUserWithEmailAndPassword(email, senha)
        .addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                mostrarErro(task.getException());
                return;
            }
            FirebaseUser user = auth.getCurrentUser();
            UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(perfil);
        });
```

Para recuperação de senha, use `auth.sendPasswordResetEmail(email)`. Antes de chamar, inicialize o campo da tela (`editEmail = findViewById(...)`); o arquivo `EsqueceuSenhaActivity` do snapshot o declara, mas não o vincula ao layout. O login Google requer `google-services.json`, o provedor ativado no Firebase Console e `default_web_client_id` gerado pelo plugin.

## 11. Intents, Bundle e diálogos

`IntentBundle` cobre comunicação entre Activities, discagem, e-mail e caixas de diálogo. Para tela interna, use intent explícita e dados simples:

```java
Intent intent = new Intent(this, InformacaoActivity.class);
intent.putExtra("nome", nome);
intent.putExtra("telefone", telefone);
startActivity(intent);

// destino
String nome = getIntent().getStringExtra("nome");
```

Para uma ação do sistema, use uma intent implícita:

```java
Intent discar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefone));
startActivity(discar);
```

`ACTION_DIAL` abre o discador sem ligar automaticamente. Para confirmação, `new AlertDialog.Builder(this).setMessage(...).setPositiveButton(...).show()` é suficiente na maioria dos casos.

## 12. MVVM e ViewModel

`MVVM` separa `View` (Fragment), `ViewModel` (validação e orquestração) e regra de conversão de temperatura. O benefício é testar a regra sem depender de Android UI.

```java
public class ConversorViewModel extends ViewModel {
    private final MutableLiveData<String> resultado = new MutableLiveData<>();
    public LiveData<String> resultado() { return resultado; }

    public void converter(String texto) {
        try {
            double celsius = Double.parseDouble(texto);
            resultado.setValue(String.format(Locale.US, "%.2f °F", celsius * 9 / 5 + 32));
        } catch (NumberFormatException e) {
            resultado.setValue("Informe um número válido");
        }
    }
}

viewModel.resultado().observe(getViewLifecycleOwner(), txt::setText);
```

O projeto original usa uma interface `Listener`; funciona para aprender o fluxo, mas `LiveData` (ou `StateFlow` em Kotlin) lida melhor com recriações por rotação de tela.

## 13. SharedPreferences e cache offline

`SharedPreferences` baixa produtos, converte a lista com Gson e guarda o JSON; se a chamada falhar, restaura o cache. É adequado para dados pequenos e não sensíveis.

```java
SharedPreferences prefs = getSharedPreferences("cache_produtos", MODE_PRIVATE);
Gson gson = new Gson();

// salvar
prefs.edit().putString("produtos", gson.toJson(produtos)).apply();

// restaurar
Type tipo = new TypeToken<List<Produto>>() {}.getType();
String json = prefs.getString("produtos", null);
List<Produto> cache = json == null ? new ArrayList<>() : gson.fromJson(json, tipo);
```

Não salve token, senha ou dados sensíveis em `SharedPreferences` comum. Para preferências pequenas que precisam de segurança, considere armazenamento criptografado; para cache e consultas maiores, Room é mais apropriado.

## 14. Bottom navigation, toolbar e mapas

`MenuButton` usa `BottomNavigationView`, `NavHostFragment`, `NavController` e `AppBarConfiguration` para sincronizar abas e botão Voltar da toolbar.

```java
NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
        .findFragmentById(R.id.nav_host_fragment);
NavController navController = host.getNavController();

NavigationUI.setupWithNavController(bottomNavigationView, navController);
AppBarConfiguration config = new AppBarConfiguration.Builder(
        R.id.homeFragment, R.id.settingsFragment, R.id.mapsFragment).build();
NavigationUI.setupActionBarWithNavController(this, navController, config);
```

O Manifest referencia `MAPS_API_KEY` via `local.properties`, que é a forma certa de não versionar a chave. Restrinja a chave de Maps por pacote Android e certificado SHA-1/SHA-256 no Google Cloud.

## 15. Câmera, galeria, MediaStore e upload de imagem

`MySelf` capta foto usando `ActivityResultContracts.TakePicture`, abre a galeria, persiste imagem no MediaStore, conserva a URI em rotação e envia a imagem ao Cloudinary. Para câmera, entregue uma URI do `FileProvider`:

```java
ActivityResultLauncher<Uri> camera = registerForActivityResult(
        new ActivityResultContracts.TakePicture(), ok -> {
            if (ok) imagem.setImageURI(fotoUri);
        });

File arquivo = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "foto_" + System.currentTimeMillis() + ".jpg");
fotoUri = FileProvider.getUriForFile(this,
        getPackageName() + ".fileprovider", arquivo);
camera.launch(fotoUri);
```

O repositório ainda redimensiona e corrige orientação antes do upload. Trate erros de leitura/escrita, feche streams com `try-with-resources` e mantenha credenciais/presets de upload fora do código quando forem sensíveis.

## 16. Tradutor

O repositório [`Tradutor`](https://github.com/projetos-mobile-android/Tradutor) estava vazio no clone analisado; portanto, não há conteúdo ou exemplo de código a extrair com fidelidade. Quando receber arquivos, o escopo esperado costuma ser: entrada de texto, chamada de API de tradução, seleção de idiomas, carregamento e tratamento de erro.

## Ordem recomendada para estudar

1. Eventos e UI: `PedraPapelTesoura`, `IntentBundle`, `SplashScreen`.
2. Navegação e listas: `Fragment`, `MenuButton`, `RecycleView`.
3. Dados remotos e cache: `Produto`, `GridPedido`, `SharedPreferences`.
4. Arquitetura e serviços: `MVVM`, `Playlist`, `FirebaseAuth`, `ChatBot`.
5. Recursos do aparelho: `WebView` e `MySelf`.

## Observações de qualidade encontradas

- Há repositórios didáticos com código incompleto ou pequenos erros de compilação; os exemplos deste guia corrigem esses pontos quando interferem no uso.
- `FirebaseAuth` foi atualizado antes da análise final; `Tradutor` continua vazio.
- Para colocar esses projetos em produção, complemente validações, estados de carregamento, tratamento de falha, regras de acesso no backend e gerenciamento de segredos.
