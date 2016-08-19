package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// TODO Refactor CourseAndExerciseManager(), setup(), updateAll() -> initiateDatabase()!!!!
/**
 * Holds a database of courses in memory, allowing quick fetching of course
 * when necessary without calling the TmcCore.
 *
 * <p>
 *   Also gives methods to update project list when necessary
 * </p>
 */
public class CourseAndExerciseManager {

    private static final Logger logger = LoggerFactory.getLogger(CourseAndExerciseManager.class);

    public CourseAndExerciseManager() {
        logger.info("CourseAndExerciseManager constructor call. @CourseAndExerciseManager");
        initiateDatabase();
    }

    public static void setup() {
        logger.info("Setup CourseAndExerciseManager. @CourseAndExerciseManager");
        initiateDatabase();

    }

    public static Exercise get(String course, String exercise) {
        logger.info("Get exercise from CourseAndExerciseManager. @CourseAndExerciseManager");
        try {
            List<Exercise> exercises = PersistentExerciseDatabase.getInstance()
                    .getExerciseDatabase().getCourses().get(course);
            for (Exercise exc : exercises) {
                if (exerciseIsTheCorrectOne(exc, exercise)) {
                    return exc;
                }
            }
        } catch (Exception exception) {
            logger.warn("Exercise was not found. @CourseAndExerciseManager",
                    exception, exception.getStackTrace());
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception, "Could not find the exercise", false);
        }
        return null;
    }

    private static boolean exerciseIsTheCorrectOne(Exercise exc, String exerciseName) {
        logger.info("Checking if {} equals {}. @CourseAndExerciseManager",
                exc.getName(), exerciseName);
        return exc.getName().equals(exerciseName);
    }

    public static List<Exercise> getExercises(String course) {
        logger.info("Getting list of exercises. @CourseAndExerciseManager");
        try {
            return PersistentExerciseDatabase.getInstance()
                    .getExerciseDatabase().getCourses().get(course);
        } catch (Exception exception) {
            logger.warn("Course was not found. @CourseAndExerciseManager",
                    exception, exception.getStackTrace());
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception, "Could not find the course.", false);
        }
        return null;
    }

    public static Map<String, List<Exercise>> getDatabase() {
        logger.info("Get database from CourseAndExerciseManager. @CourseAndExerciseManager");
        return PersistentExerciseDatabase.getInstance().getExerciseDatabase().getCourses();
    }

    static void initiateDatabase() {
        logger.info("Initiating database. Fetching courses from TmcCore."
                + " @CourseAndExerciseManager");
        try {
            Map<String, List<Exercise>> database = new HashMap<>();
            List<Course> courses = (ArrayList<Course>) TmcCoreHolder.get()
                    .listCourses(ProgressObserver.NULL_OBSERVER).call();

            for (Course course : courses) {
                List<Exercise> exercises;
                try {
                    logger.info("Fetching {} from TmcCore. @CourseAndExerciseManager", course);
                    course = TmcCoreHolder.get()
                            .getCourseDetails(ProgressObserver.NULL_OBSERVER, course).call();
                    exercises = (ArrayList<Exercise>) new CheckForExistingExercises()
                            .getListOfDownloadedExercises(course.getExercises(),
                                    TmcSettingsManager.get());
                    database.put(course.getName(), exercises);
                } catch (Exception exception) {
                    logger.warn("Failed to initiate database. @CourseAndExerciseManager",
                            exception, exception.getStackTrace());
                    errorMessageService(exception);
                }
            }

            PersistentExerciseDatabase.getInstance().getExerciseDatabase().setCourses(database);
        } catch (TmcCoreException exception) {
            logger.warn("Failed to fetch courses from TmcCore. @CourseAndExerciseManager",
                    exception, exception.getStackTrace());
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception, false);
            refreshCoursesOffline();
        } catch (Exception exception) {
            logger.warn("Failed to fetch courses from TmcCore. @CourseAndExerciseManager",
                    exception, exception.getStackTrace());
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception, "Failed to fetch courses from tmc core.", false);
            refreshCoursesOffline();
        }
    }

    private static void refreshCoursesOffline() {
        logger.info("Refreshing side panel course view for user while offline. "
                + " @CourseAndExerciseManager");
        Map<String, List<Exercise>> courses = getExerciseDatabase().getCourses();

        for (String courseName : courses.keySet()) {
            List<Exercise> exercises = courses.get(courseName);

            removeExercisesNotFoundFromLocalDirectories(exercises, courseName);
        }
        getExerciseDatabase().setCourses(courses);
    }

    private static void removeExercisesNotFoundFromLocalDirectories(List<Exercise> exercises,
                                                                    String courseName) {
        logger.info("Removing all exercises from the side panel, that are not "
                + "found from local directories . @CourseAndExerciseManager");
        List<String> exerciseNamesThroughDirectories =
                getExerciseNamesThroughDirectories(courseName);

        Iterator<Exercise> iterator = exercises.iterator();

        while (iterator.hasNext()) {
            Exercise exercise = iterator.next();

            if (!exerciseNamesThroughDirectories.contains(exercise.getName())) {
                logger.info("Removed {}. @CourseAndExerciseManager", exercise.getName());
                iterator.remove();
            }

        }
    }

    private static List<String> getExerciseNamesThroughDirectories(String courseName) {
        logger.info("Fetching {} course exercise names from the local "
                + "directories. @CourseAndExerciseManager", courseName);
        return new ObjectFinder().listAllDownloadedExercises(courseName);
    }

    private static ExerciseDatabase getExerciseDatabase() {
        logger.info("Get ExerciseDatabase from PersistentExerciseDatabase. "
                + "@CourseAndExerciseManager");
        return PersistentExerciseDatabase.getInstance().getExerciseDatabase();
    }


    public static void updateSingleCourse(String courseName, CheckForExistingExercises checker,
                                          ObjectFinder finder,
                                          SettingsTmc settings) {
        logger.info("Updating single course. @CourseAndExerciseManager");
        boolean isNewCourse = getExerciseDatabase().getCourses().get(courseName) == null;
        Course course = finder.findCourseByName(courseName, TmcCoreHolder.get());

        if (course == null) {
            return;
        }

        List<Exercise> existing = (ArrayList<Exercise>) checker
                .getListOfDownloadedExercises(course.getExercises(), settings);

        getExerciseDatabase().getCourses().put(courseName, existing);

        if (isNewCourse) {
            ProjectListManager.refreshAllCourses();
        } else {
            ProjectListManager.refreshCourse(courseName);
        }
    }

    public static void updateAll() {
        logger.info("UpdateAll call. @CourseAndExerciseManager");
        initiateDatabase();
    }

    private static void errorMessageService(Exception exception) {
        logger.info("Redirecting local method error to ErrorMessageService. "
                + "@CourseAndExerciseManager");
        ErrorMessageService error = new ErrorMessageService();
        error.showMessage(exception, "Could not initiate database.", true);
    }

    public static boolean isCourseInDatabase(String string) {
        logger.info("Checking if course exists in the database."
                + " @CourseAndExerciseManager", string);
        return PersistentExerciseDatabase.getInstance()
                .getExerciseDatabase().getCourses().containsKey(string);
    }
}
