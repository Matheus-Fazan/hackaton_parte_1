# Desafio Self · Tirar a foto e mandar para a nuvem

O objetivo aqui é pequeno e concreto: tocar num botão, abrir a câmera ou galeria, tirar uma foto e receber de volta um endereço https que funciona no navegador.

A foto não fica no celular. Ela sobe para o Cloudinary, que devolve uma URL. Essa URL é o que interessa, porque é ela que vai para o banco na etapa seguinte.

## Dados de acesso

String cloudname = "dikitk54o"; String upProjeto = "Tiktoktech";

Atenção às maiúsculas. O preset é Tiktoktech, com T maiúsculo e o resto minúsculo. Se digitar TikTokTech o envio falha, e a mensagem de erro não deixa isso óbvio. Outro ponto, você deve utilizar a opção “FOLDER” e indicar “tiktoktech_salaD” use a letra da sua turma D, E, F, G, H ou I.

## O que precisa estar funcionando

1. O botão abre a câmera do aparelho ou a galeria e a foto é selecionada.

2. O envio para o Cloudinary termina sem erro.

3. A URL devolvida aparece em algum lugar da tela, nem que seja num TextView provisório.

## Como provar que funcionou

Copie a URL que apareceu e cole no navegador. Se a sua foto abrir, a etapa 1 está fechada. Se der erro 404 ou uma página em branco, ainda não.

O professor vai acompanhar no Cloudinary se a sua foto subiu..!!!