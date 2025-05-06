
import androidx.room.*


@Dao
interface RegistroPontoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registro: RegistroPonto)

    @Update
    suspend fun atualizar(registro: RegistroPonto)

    @Query("SELECT * FROM RegistroPonto ORDER BY id DESC")
    suspend fun listarTodos(): List<RegistroPonto>

    @Query("SELECT * FROM registroPonto WHERE data = :data LIMIT 1")
    suspend fun buscarPorData(data: String): RegistroPonto?

}
