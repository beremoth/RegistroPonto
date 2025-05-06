
import androidx.room.*


@Dao
interface RegistroPontoDao {
    @Insert
    suspend fun inserir(registro: RegistroPonto)

    @Update
    suspend fun atualizar(registro: RegistroPonto)

    @Query("SELECT * FROM RegistroPonto ORDER BY id DESC")
    suspend fun listarTodos(): List<RegistroPonto>
}
