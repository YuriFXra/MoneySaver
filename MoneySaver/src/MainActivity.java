import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Expense> expenseList;
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        expenseList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        expenseAdapter = new ExpenseAdapter(expenseList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(expenseAdapter);

        // Adicione alguns gastos de exemplo
        addSampleExpenses();
    }

    private void addSampleExpenses() {
        Expense expense1 = new Expense("Almoço", 15.0, "Alimentação", new Date());
        Expense expense2 = new Expense("Gasolina", 50.0, "Transporte", new Date());
        Expense expense3 = new Expense("Cinema", 25.0, "Entretenimento", new Date());

        databaseHelper.addExpense(expense1);
        databaseHelper.addExpense(expense2);
        databaseHelper.addExpense(expense3);

        expenseList.addAll(databaseHelper.getAllExpenses());
        expenseAdapter.notifyDataSetChanged();
    }

    // Classe Expense
    public static class Expense {
        private String description;
        private double amount;
        private String category;
        private Date date;

        public Expense(String description, double amount, String category, Date date) {
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        // Getters e setters
    }

    // Classe DatabaseHelper
    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "money_saver.db";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_EXPENSES = "expenses";
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_DESCRIPTION = "description";
        private static final String COLUMN_AMOUNT = "amount";
        private static final String COLUMN_CATEGORY = "category";
        private static final String COLUMN_DATE = "date";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableQuery = "CREATE TABLE " + TABLE_EXPENSES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_AMOUNT + " REAL," +
                    COLUMN_CATEGORY + " TEXT," +
                    COLUMN_DATE + " TEXT" +
                    ")";
            db.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_EXPENSES;
            db.execSQL(dropTableQuery);
            onCreate(db);
        }

        public void addExpense(Expense expense) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_DESCRIPTION, expense.description);
            values.put(COLUMN_AMOUNT, expense.amount);
            values.put(COLUMN_CATEGORY, expense.category);
            values.put(COLUMN_DATE, expense.date.toString());
            db.insert(TABLE_EXPENSES, null, values);
            db.close();
        }

        public List<Expense> getAllExpenses() {
            List<Expense> expenseList = new ArrayList<>();
            String selectQuery = "SELECT * FROM " + TABLE_EXPENSES;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                    double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));
                    String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                    String dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                    Date date = new Date(dateString);

                    Expense expense = new Expense(description, amount, category, date);
                    expenseList.add(expense);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return expenseList;
        }
    }

    // Classe ExpenseAdapter
    public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
        private List<Expense> expenseList;

        public ExpenseAdapter(List<Expense> expenseList) {
            this.expenseList = expenseList;
        }

        @NonNull
        @Override
        public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expense, parent, false);
            return new ExpenseViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
            Expense expense = expenseList.get(position);
            holder.txtDescription.setText(expense.description);
            holder.txtAmount.setText(String.valueOf(expense.amount));
            holder.txtCategory.setText(expense.category);
            holder.txtDate.setText(expense.date.toString());
        }

        @Override
        public int getItemCount() {
            return expenseList.size();
        }

        public void notifyDataSetChanged() {
        }

        public class ExpenseViewHolder extends RecyclerView.ViewHolder {
            public TextView txtDescription;
            public TextView txtAmount;
            public TextView txtCategory;
            public TextView txtDate;

            public ExpenseViewHolder(View itemView) {
                super();
                txtDescription = itemView.findViewById(R.id.txtDescription);
                txtAmount = itemView.findViewById(R.id.txtAmount);
                txtCategory = itemView.findViewById(R.id.txtCategory);
                txtDate = itemView.findViewById(R.id.txtDate);
            }
        }
    }
}
