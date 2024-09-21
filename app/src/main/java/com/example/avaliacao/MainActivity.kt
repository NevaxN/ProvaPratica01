package com.example.avaliacao

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayoutPreview()
        }
    }
}

@Composable
fun CadastroProduto(navController: NavController) {
    val context = LocalContext.current
    var nomeProduto by remember{mutableStateOf("")}
    var categoria by remember{ mutableStateOf("") }
    var preco by remember{ mutableStateOf<Double?>(null) }
    var qtdEstoque by remember { mutableStateOf<Int?>(null) }

    Column (modifier= Modifier
        .fillMaxSize()
        .padding(30.dp), verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){

        TextField(modifier = Modifier.fillMaxWidth(), value = nomeProduto,
            onValueChange = { nomeProduto = it},
            label = { Text(text = "Informe o nome")}
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(modifier = Modifier.fillMaxWidth(), value = categoria,
            onValueChange = { categoria = it},
            label = { Text(text = "Informe a categoria")}
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(modifier = Modifier.fillMaxWidth(), value = preco?.toString() ?:"",
            onValueChange = {
            preco = it.toDoubleOrNull()
                                                 },
            label = { Text(text = "Informe o preco")}
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(modifier = Modifier.fillMaxWidth(), value = qtdEstoque?.toString() ?:"",
            onValueChange = {
            qtdEstoque = it.toIntOrNull()
                                                                       },
            label = { Text(text = "Informe a Quantidade em Estoque")}
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (nomeProduto.isBlank() && categoria.isBlank() && preco == null &&
                qtdEstoque == null ) {
                Toast.makeText(context, "Esse campo precisa ser preenchido!",
                    Toast.LENGTH_SHORT).show()
            }else{
                if(qtdEstoque!! <= 0 || preco!! <= 0){
                    Toast.makeText(context, "Quantidade e preço devem ser maiores que 0",
                        Toast.LENGTH_SHORT).show()
                }else{
                    Estoque.adicionarProduto(Produto(nomeProduto, categoria, preco!!,
                        qtdEstoque!!))
                    navController.navigate("listarProdutos")
                    nomeProduto = ""
                    categoria = ""
                    preco = null
                    qtdEstoque = null
                }
            }
        }) {
            Text(text = "Salvar")
        }
    }
}

@Composable
fun ListarProdutos(navController: NavController){
    var produtos = Estoque.listarProdutos()
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){
        LazyColumn {
            items(produtos){ produto ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(15.dp),
                        text = "${produto.nomeProduto} (${produto.preco} unidades)")
                    Spacer(modifier = Modifier.width(15.dp))
                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("detalhesProdutos/$produtoJson")
                    }) {
                        Text(text = "Detalhes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val valorTotal = Estoque.calcularValorTotalEstoque()
            val quantidadeTotal = Estoque.calcularQuantidadeTotalProdutos()
            navController.navigate("estatisticas/$valorTotal&&$quantidadeTotal")

        }) {
            Text("Ver Estatísticas")
        }
    }

}

@Composable
fun DetalhesProdutos(navController: NavController, produto: Produto){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Nome do Produto: ${produto.nomeProduto}")
        Text(text = "Categoria: ${produto.categoria}")
        Text(text = "Preço: ${produto.preco}")
        Text(text = "Quantidade em Estoque: ${produto.qtdEstoque}")

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "Voltar")
        }
    }
}

@Composable
fun EstatisticasProdutos(navController: NavController, valorTotal: Float, quantidadeTotal: Int){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Valor Total do Estoque: $valorTotal")
        Text(text = "Quantidade Total de Produtos: $quantidadeTotal")

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "Voltar")
        }
    }
}

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "cadastroProduto"){
        composable("cadastroProduto") { CadastroProduto(navController) }
        composable("listarProdutos") { ListarProdutos(navController) }
        composable("detalhesProdutos/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produto::class.java)
            DetalhesProdutos(navController, produto)
        }
        composable("estatisticas/{valorTotal}&&{quantidadeTotal}") { backStackEntry ->
            val valorTotalString = backStackEntry.arguments?.getString("valorTotal")
            val quantidadeTotalString = backStackEntry.arguments?.getString("quantidadeTotal")

            val valorTotal = valorTotalString?.toFloatOrNull()
            val quantidadeTotal = quantidadeTotalString?.toIntOrNull()

            if (valorTotal != null && quantidadeTotal != null) {
                EstatisticasProdutos(navController, valorTotal, quantidadeTotal)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LayoutPreview() {
    AppNavigation()
}