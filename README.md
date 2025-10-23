[README.md](https://github.com/user-attachments/files/23079306/README.md)
# App de Previsão do Tempo com GPS

App Android simples que obtém sua localização via GPS e exibe a previsão do tempo atual.

## 🎯 Requisitos Atendidos

✅ Acesso GPS (retorna latitude e longitude)  
✅ Chamada à API HGBrasil  
✅ Identificação de Sol/Chuva/Nublado  
✅ Exibição de imagem na tela  
✅ Salvar em SQLite (previsão, latitude, longitude e data)

## 📁 Estrutura de Arquivos

```
ProjetoGPS/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/projetogps/
│   │   │   │   ├── MainActivity.java
│   │   │   │   └── DatabaseHelper.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_sol.xml
│   │   │   │   │   ├── ic_nublado.xml
│   │   │   │   │   └── ic_chuva.xml
│   │   │   │   └── values/
│   │   │   │       └── strings.xml
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
```

## 🔧 Configuração

### 1. Obter Chave da API HGBrasil

1. Acesse: https://hgbrasil.com/
2. Crie uma conta gratuita
3. Copie sua chave API

### 2. Configurar a Chave no App

Abra `MainActivity.java` e substitua na linha 92:

```java
String apiUrl = "https://api.hgbrasil.com/weather?key=SUA_CHAVE_AQUI&lat=" 
```

Por:

```java
String apiUrl = "https://api.hgbrasil.com/weather?key=SUA_CHAVE_REAL&lat=" 
```

### 3. Copiar Arquivos

**Java (src/main/java/com/example/projetogps/):**
- MainActivity.java
- DatabaseHelper.java

**Layout (src/main/res/layout/):**
- activity_main.xml

**Ícones (src/main/res/drawable/):**
- ic_sol.xml
- ic_nublado.xml
- ic_chuva.xml

**Manifesto (src/main/):**
- AndroidManifest.xml

**Build (app/):**
- build.gradle

## 📱 Como Usar

1. Abra o app
2. Conceda permissão de localização quando solicitado
3. O app automaticamente:
   - Obtém sua localização GPS
   - Busca a previsão do tempo
   - Exibe o ícone correspondente (☀️ 🌥️ 🌧️)
   - Mostra temperatura e descrição
   - Salva os dados no banco SQLite

## 🗄️ Banco de Dados

Tabela: `weather_data`

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | INTEGER | Chave primária |
| latitude | REAL | Latitude da localização |
| longitude | REAL | Longitude da localização |
| previsao | TEXT | Descrição do tempo |
| temperatura | INTEGER | Temperatura em °C |
| data | DATETIME | Data e hora do registro |

## 🎨 Ícones

- **Sol** (☀️): Dias ensolarados, céu limpo
- **Nublado** (🌥️): Parcialmente nublado, nuvens
- **Chuva** (🌧️): Chuva, tempestades

## ⚙️ Permissões Necessárias

- `ACCESS_FINE_LOCATION`: Para obter localização GPS precisa
- `ACCESS_COARSE_LOCATION`: Para obter localização aproximada
- `INTERNET`: Para fazer chamadas à API

## 📋 Dependências

```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'com.google.android.gms:play-services-location:21.1.0'
```

## 🐛 Solução de Problemas

### Erro de Localização
- Verifique se o GPS está ativado
- Confirme que concedeu permissão de localização

### Erro na API
- Verifique se colocou a chave correta
- Confirme que tem conexão com internet
- Verifique se não excedeu o limite de requisições da API

### App não compila
- Verifique se o package em todos os arquivos é `com.example.projetogps`
- Sincronize o Gradle (Sync Now)
- Limpe o projeto (Build > Clean Project)

## 📝 Notas

- API gratuita tem limite de requisições
- App requer Android 7.0 (API 24) ou superior
- Funciona melhor ao ar livre para GPS preciso
- Internet necessária para buscar previsão

## 🚀 Melhorias Futuras

- [ ] Previsão para múltiplos dias
- [ ] Histórico de previsões
- [ ] Atualização automática periódica
- [ ] Múltiplas localizações
- [ ] Gráficos de temperatura
