package com.inguana.vocabularypractice.Room.Migrations;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;

import com.google.firebase.firestore.auth.User;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class Migration1To2Test {

    /*@Test
    public void migrate() {
    }

    //https://medium.com/androiddevelopers/testing-room-migrations-be93cdb0d975
    @Test
    public void migrationFrom1To2_containsCorrectData() throws IOException {
        String TEST_DB_NAME = "Sample.db";

        @Rule
        public MigrationTestHelper testHelper =
                new MigrationTestHelper(
                        InstrumentationRegistry.getInstrumentation(),
                        <your_database_class>.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());

        SupportSQLiteDatabase db =
                testHelper.createDatabase(TEST_DB_NAME, 1);

        db = testHelper.runMigrationsAndValidate(TEST_DB_NAME, 4, validateDroppedTables, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4);

        // Create the database in version 1
        SupportSQLiteDatabase db =
                testHelper.createDatabase(TEST_DB_NAME, 1);
        // Insert some data
        insertUser(USER.getId(), USER.getUserName(), db);
        //Prepare for the next version
        db.close();

        // Re-open the database with version 3 and provide MIGRATION_1_2
        // and MIGRATION_2_3 as the migration process.
        testHelper.runMigrationsAndValidate(TEST_DB_NAME, 3,
                validateDroppedTables, MIGRATION_1_2, MIGRATION_2_3);

        // MigrationTestHelper automatically verifies the schema
        //changes, but not the data validity
        // Validate that the data was migrated properly.
        User dbUser = getMigratedRoomDatabase().userDao().getUser();
        assertEquals(dbUser.getId(), USER.getId());
        assertEquals(dbUser.getUserName(), USER.getUserName());
        // The date was missing in version 2, so it should be null in
        //version 3
        assertEquals(dbUser.getDate(), null);
    }*/
}