package com.example.avaliacao

class Estoque {
    companion object{
        private val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto){
            listaProdutos.add(produto)
        }

        fun listarProdutos(): MutableList<Produto>{
            return listaProdutos
        }

        fun calcularValorTotalEstoque(): Double {
            return listaProdutos.sumOf { it.preco * it.qtdEstoque.toDouble() }
        }

        fun calcularQuantidadeTotalProdutos(): Int{
            return listaProdutos.sumOf { it.qtdEstoque }
        }
    }
}