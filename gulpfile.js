var hbsp = require("hbsp");
var vdev = require("vdev");
var path = require("path");
var concat = require('gulp-concat');
var gulp = require("gulp");
var less = require("gulp-less");
var del = require("del");
var fs = require("fs");


var hbsPrecompile = hbsp.precompile;

var dbPrefix = "samplesocial";
var webappDir = "src/main/webapp/";
var sqlDir = "src/main/webapp/WEB-INF/sql/";

var cssDir = path.join(webappDir,"/css/");

gulp.task('default',['clean', 'hbs', 'less']);

// --------- Web Assets Processing --------- //
gulp.task('watch', ['default'], function(){
    gulp.watch(path.join(webappDir,"/tmpl/",'*.tmpl'), ['hbs']);

    gulp.watch(path.join(webappDir,"/less/",'*.less'), ['less']);

});

gulp.task('clean', function(){
    var dirs = [cssDir];

    // make sure the directories exists (they might not in fresh clone)
    var dir;
    for (var i = 0; i < dirs.length ; i ++){
        dir = dirs[i];
        if (!fs.existsSync(dir)) {
            fs.mkdir(dir);
        }
        del.sync(dir + "all.css");
    }
});

gulp.task('hbs', function() {
    gulp.src(path.join(webappDir,"/tmpl/",'*.tmpl'))
        .pipe(hbsPrecompile())
        .pipe(concat("templates.js"))
        .pipe(gulp.dest(path.join(webappDir,"/js/")));
});

gulp.task('less', function() {
    gulp.src(path.join(webappDir,"/less/",'all.less'))
        .pipe(less())
        .pipe(gulp.dest(cssDir));
});
// --------- /Web Assets Processing --------- //

gulp.task('recreateDb', function(){
    vdev.psql("postgres", null, "postgres", vdev.listSqlFiles(sqlDir,{to:0}));
    vdev.psql(dbPrefix + "_user", null, dbPrefix + "_db", vdev.listSqlFiles(sqlDir,{from:1}));
});