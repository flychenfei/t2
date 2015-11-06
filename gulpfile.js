var hbsp = require("hbsp");
var vdev = require("vdev");
var path = require("path");
var concat = require('gulp-concat');
var gulp = require("gulp");
var less = require("gulp-less");
var del = require("del");
var fs = require("fs");
var postcss = require('gulp-postcss');
var autoprefixer = require('autoprefixer');
var cssnext = require('cssnext');
var precss = require('precss');
var postcssSimpleVars = require("postcss-simple-vars");
var postcssMixins = require("postcss-mixins");
var postcssNested = require("postcss-nested");
var sourcemaps = require("gulp-sourcemaps");
var atImport = require("postcss-import");
var lessVars = require("postcss-less-vars");
var customProperties = require("postcss-custom-properties");


var hbsPrecompile = hbsp.precompile;

var dbPrefix = "samplesocial";
var webappDir = "src/main/webapp/";
var sqlDir = "src/main/webapp/WEB-INF/sql/";

var cssDir = path.join(webappDir,"/css/");

gulp.task('default',['clean', 'hbs', 'pcss']);

// --------- Web Assets Processing --------- //
gulp.task('watch', ['default'], function(){
	gulp.watch(path.join(webappDir,"/tmpl/",'*.tmpl'), ['hbs']);

	// gulp.watch(path.join(webappDir,"/less/",'all.less'), ['pcss']);
	gulp.watch(path.join(webappDir,"/pcss/",'all.pcss'), ['pcss']);

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

// gulp.task('less', function() {
// 	gulp.src(path.join(webappDir,"/less/",'all.less'))
// 		.pipe(less())
// 		.pipe(gulp.dest(cssDir));
// });
// --------- /Web Assets Processing --------- //

gulp.task('recreateDb', function(){
	vdev.psql("postgres", null, "postgres", vdev.listSqlFiles(sqlDir,{to:0}));
	vdev.psql(dbPrefix + "_user", null, dbPrefix + "_db", vdev.listSqlFiles(sqlDir,{from:1}));
});

gulp.task('pcss', function () {
	var processors = [
		postcssMixins,
		postcssSimpleVars,
		postcssNested,
		autoprefixer,
		customProperties
	];
  return gulp.src(path.join(webappDir,"/pcss/",'all.pcss'))
	.pipe(less())
	.pipe(postcss(processors))
	.pipe(gulp.dest(cssDir));
});